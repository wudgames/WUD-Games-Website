import React, { useEffect, useRef, useState } from "react";
import {
  Route,
  Routes,
  BrowserRouter as Router,
  NavLink,
  Navigate,
} from "react-router";
import {
  Menu,
  Sun,
  Moon,
  GamepadIcon,
  LogIn,
  LogOut,
  Dice6,
  Wrench,
  Monitor,
} from "lucide-react";
import { AuthProvider, useAuth } from "./AuthContext";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import ConsolegameMain from "@/consolegames/ConsolegameMain";
import BoardgameMain from "@/boardgames/BoardgameMain";
import EquipmentMain from "@/equipment/EquipmentMain";
import SteamMain from "@/steam/SteamMain";
import { GameManagerProvider } from "@/boardgames/GameManagerContext";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";

export const LoginButton = () => {
  const { auth, login, logout, version } = useAuth();
  const [showLogin, setShowLogin] = useState(false);
  const [error, setError] = useState("");
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });
  const [appVersion, setAppVersion] = useState("Unknown");
  const loginRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        loginRef.current &&
        !loginRef.current.contains(event.target as Node)
      ) {
        setShowLogin(false);
        setError("");
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (auth || !showLogin) {
      setError("");
    }
    if (showLogin) {
      (async () => {
        const data = await version();
        if (data) {
          setAppVersion(data.version);
        }
      })();
    }
  }, [auth, showLogin]);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await login(credentials.username, credentials.password);
      setShowLogin(false);
    } catch (error: unknown) {
      setError(error instanceof Error ? error.message : "Login failed");
    }
  };

  return (
    <div className="z-10">
      {!auth ? (
        <div className="relative" ref={loginRef}>
          <Button
            onClick={() => setShowLogin(!showLogin)}
            variant="secondary"
            size="sm"
            className="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white border-0"
          >
            <LogIn className="w-4 h-4" /> Login
          </Button>

          {showLogin && (
            <Card className="absolute right-0 mt-2 w-72 shadow-xl border border-border/50 z-50">
              <CardContent className="p-4">
                <form onSubmit={handleLogin} className="space-y-3">
                  {error && (
                    <Alert variant="destructive" className="py-2">
                      <AlertDescription>{error}</AlertDescription>
                    </Alert>
                  )}

                  <Input
                    type="text"
                    placeholder="Username"
                    onChange={(e) =>
                      setCredentials({
                        ...credentials,
                        username: e.target.value,
                      })
                    }
                  />

                  <Input
                    type="password"
                    placeholder="Password"
                    onChange={(e) =>
                      setCredentials({
                        ...credentials,
                        password: e.target.value,
                      })
                    }
                  />

                  <Button type="submit" className="w-full">
                    Login
                  </Button>

                  <p className="text-xs text-muted-foreground text-center">
                    v{appVersion}
                  </p>
                </form>
              </CardContent>
            </Card>
          )}
        </div>
      ) : (
        <div className="flex items-center gap-3">
          <span className="text-sm text-white/80 hidden sm:inline">
            {auth.username}
          </span>
          <Button
            onClick={logout}
            variant="secondary"
            size="sm"
            className="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white border-0"
          >
            <LogOut className="w-4 h-4" /> Logout
          </Button>
        </div>
      )}
    </div>
  );
};

const navItems = [
  { to: "/board-games", label: "Board Games", icon: Dice6 },
  { to: "/video-games", label: "Video Games", icon: GamepadIcon },
  { to: "/equipment", label: "Equipment", icon: Wrench },
  { to: "/steam", label: "Steam", icon: Monitor },
];

const UnifiedTopBar = () => {
  return (
    <header className="fixed top-0 left-0 w-full bg-menubar text-white z-50 shadow-lg">
      <div className="max-w-screen-2xl mx-auto px-4 sm:px-6">
        <div className="flex items-center justify-between h-14">
          {/* Brand */}
          <NavLink
            to="/"
            className="flex items-center gap-2 text-lg font-bold tracking-tight shrink-0"
          >
            <img src="/logo.png" alt="Logo" className="h-8 w-8" />
            <span className="hidden sm:inline">WUD Games</span>
          </NavLink>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `flex items-center gap-2 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                    isActive
                      ? "bg-white/20 text-white"
                      : "text-white/70 hover:text-white hover:bg-white/10"
                  }`
                }
              >
                <item.icon className="w-4 h-4" />
                {item.label}
              </NavLink>
            ))}
          </nav>

          {/* Right side: theme toggle + auth + mobile menu */}
          <div className="flex items-center gap-2">
            {/* Theme Toggle */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="text-white/70 hover:text-white hover:bg-white/10"
                >
                  <Sun className="h-4 w-4 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
                  <Moon className="absolute h-4 w-4 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
                  <span className="sr-only">Toggle theme</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => setTheme("light")}>
                  Light
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => setTheme("dark")}>
                  Dark
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => setTheme("auto")}>
                  System
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            <LoginButton />

            {/* Mobile Menu */}
            <div className="md:hidden">
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="text-white/70 hover:text-white hover:bg-white/10"
                  >
                    <Menu className="h-5 w-5" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-48">
                  {navItems.map((item) => (
                    <DropdownMenuItem key={item.to} asChild>
                      <NavLink to={item.to} className="flex items-center gap-2">
                        <item.icon className="w-4 h-4" />
                        {item.label}
                      </NavLink>
                    </DropdownMenuItem>
                  ))}
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export const setTheme = (theme: "light" | "dark" | "auto") => {
  const htmlElement = document.documentElement;
  htmlElement.classList.remove("light", "dark");

  if (theme === "auto") {
    localStorage.removeItem("theme");
    const prefersDark = window.matchMedia(
      "(prefers-color-scheme: dark)",
    ).matches;
    htmlElement.classList.toggle("dark", prefersDark);
    htmlElement.classList.toggle("light", !prefersDark);
  } else {
    localStorage.theme = theme;
    htmlElement.classList.add(theme);
  }
};

document.addEventListener("DOMContentLoaded", () => {
  const savedTheme = localStorage.theme as "light" | "dark" | undefined;
  if (savedTheme) {
    setTheme(savedTheme);
  } else {
    setTheme("auto");
  }
});

const App = () => {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen antialiased">
          <UnifiedTopBar />
          <main className="pt-14">
            <Routes>
              <Route
                path="/"
                element={<Navigate to="/board-games" replace />}
              />
              <Route
                path="/board-games/*"
                element={
                  <GameManagerProvider>
                    <BoardgameMain />
                  </GameManagerProvider>
                }
              />
              <Route path="/video-games/*" element={<ConsolegameMain />} />
              <Route path="/equipment/*" element={<EquipmentMain />} />
              <Route path="/steam/*" element={<SteamMain />} />
            </Routes>
          </main>
        </div>
      </AuthProvider>
    </Router>
  );
};

export default App;
