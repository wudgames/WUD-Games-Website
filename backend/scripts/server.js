const fs = require('fs');
const path = require('path');
const { spawn, spawnSync } = require('child_process');

const root = path.resolve(__dirname, '..', '..');
const configDir = path.join(root, 'config');
const backendDir = path.join(root, 'backend');
const composeFile = path.join(root, 'database', 'compose.yaml');
const exampleEnvPath = path.join(configDir, '.env.example');
const localEnvPath = path.join(configDir, '.env');

function readEnvFile(filePath) {
  const values = {};
  const content = fs.readFileSync(filePath, 'utf8');

  for (const line of content.split(/\r?\n/)) {
    const match = line.match(/^\s*([^#=\s]+)\s*=\s*(.*?)\s*$/);
    if (!match) {
      continue;
    }

    let value = match[2];
    if (
      value.length >= 2 &&
      ((value.startsWith('"') && value.endsWith('"')) ||
        (value.startsWith("'") && value.endsWith("'")))
    ) {
      value = value.slice(1, -1);
    }
    values[match[1]] = value;
  }

  return values;
}

function runCompose(args) {
  const result = spawnSync('docker', [
    'compose',
    '-p',
    'wud-games-website',
    '-f',
    composeFile,
    ...args
  ], {
    cwd: root,
    stdio: 'inherit'
  });
  if (result.error) {
    throw result.error;
  }
  if (result.status !== 0) {
    throw new Error(`docker compose ${args.join(' ')} failed`);
  }
}

function buildApplication(env) {
  const isWindows = process.platform === 'win32';
  const command = isWindows ? process.env.ComSpec || 'cmd.exe' : './mvnw';
  const args = isWindows
    ? ['/d', '/s', '/c', '"mvnw.cmd -DskipTests package"']
    : ['-DskipTests', 'package'];
  const result = spawnSync(command, args, {
    cwd: backendDir,
    env,
    windowsVerbatimArguments: isWindows,
    stdio: 'inherit'
  });

  if (result.error) {
    throw result.error;
  }
  if (result.status !== 0) {
    throw new Error('Maven package failed');
  }

  const targetDir = path.join(backendDir, 'target');
  const jars = fs.readdirSync(targetDir)
    .filter((name) => name.endsWith('.jar') && !name.endsWith('.jar.original'));
  if (jars.length !== 1) {
    throw new Error(`Expected one application JAR in ${targetDir}, found ${jars.length}`);
  }

  return path.join(targetDir, jars[0]);
}

function main() {
  fs.mkdirSync(configDir, { recursive: true });
  if (!fs.existsSync(localEnvPath)) {
    fs.copyFileSync(exampleEnvPath, localEnvPath);
    console.log('Created config/.env from config/.env.example.');
  }

  const env = {
    ...readEnvFile(exampleEnvPath),
    ...readEnvFile(localEnvPath),
    ...process.env
  };

  env.SPRING_PROFILES_ACTIVE = env.SPRING_PROFILES_ACTIVE || 'local';
  const applicationJar = buildApplication(env);
  // Compose is started explicitly, and packaged runs do not need classpath restarts.
  env.SPRING_DOCKER_COMPOSE_ENABLED = 'false';
  env.SPRING_DEVTOOLS_RESTART_ENABLED = 'false';

  runCompose(['up', '-d']);
  console.log('Starting the web app. Press Ctrl+C to stop it; run npm run stop to stop its services.');
  const app = spawn('java', ['-jar', applicationJar], {
    cwd: backendDir,
    env,
    stdio: 'inherit'
  });

  let stopping = false;
  process.on('SIGINT', () => {
    stopping = true;
    app.kill('SIGINT');
  });
  process.on('SIGTERM', () => {
    stopping = true;
    app.kill('SIGTERM');
  });

  app.on('error', (error) => {
    console.error(`Could not start the application: ${error.message}`);
  });

  app.on('close', (code, signal) => {
    process.exitCode =
      stopping || signal === 'SIGINT' || signal === 'SIGTERM' ? 0 : (code ?? 1);
  });
}

try {
  main();
} catch (error) {
  console.error(error.message);
  process.exitCode = 1;
}
