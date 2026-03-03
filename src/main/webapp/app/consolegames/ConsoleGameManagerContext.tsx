import React, {
  createContext,
  useContext,
  useEffect,
  useRef,
  useCallback,
  useState,
} from "react";
import { useAuth } from "@/AuthContext";

const API_BASE_URL = "/api/consoles";
const POLL_INTERVAL_MS = 30000;

export interface Console {
  // Added export
  id?: number;
  name: string;
}

export interface ConsoleGenre {
  id: number;
  name: string;
}

export interface ConsoleGame {
  id: number;
  name: string;
  boxImageUrl: string;
  genres: ConsoleGenre[];
  releaseDate: string;
  consoles: Console[];
  description: string;
}

interface FilterState {
  gameName: string;
  consoleId: number | null;
}

interface ConsoleContextType {
  consoles: Console[];
  games: ConsoleGame[];
  filteredGames: ConsoleGame[];
  allGenres: ConsoleGenre[];
  loading: boolean;
  filters: FilterState;
  fetchConsoles: () => void;
  fetchGames: () => void;
  fetchGenres: () => void;
  createConsole: (console: Console) => void;
  updateConsole: (id: number, console: Console) => void;
  deleteConsole: (id: number) => void;
  createGame: (payload: ConsoleGamePayload) => Promise<void>;
  updateGame: (id: number, payload: ConsoleGamePayload) => Promise<void>;
  deleteGame: (id: number) => void;
  setFilters: (filters: Partial<FilterState>) => void;
}

// Interface for the payload sent to create/update endpoints
export interface ConsoleGamePayload {
  name: string;
  boxImageUrl?: string; // Optional fields marked
  description?: string;
  releaseDate?: string;
  consoleIds: number[];
  genreIds: number[];
  newGenreNames: string[];
}

const ConsoleContext = createContext<ConsoleContextType | undefined>(undefined);

export const useConsoleContext = () => {
  const context = useContext(ConsoleContext);
  if (!context) {
    throw new Error("useConsoleContext must be used within a ConsoleProvider");
  }
  return context;
};

export const ConsoleProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { auth } = useAuth();
  const [consoles, setConsoles] = useState<Console[]>([]);
  const [games, setGames] = useState<ConsoleGame[]>([]);
  const [allGenres, setAllGenres] = useState<ConsoleGenre[]>([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFiltersState] = useState<FilterState>({
    gameName: "",
    consoleId: null,
  });

  const isFetchingRef = useRef(false);
  const pollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const filteredGames = games.filter((game) => {
    const matchesName = game.name
      .toLowerCase()
      .includes(filters.gameName.toLowerCase());
    const matchesConsole =
      filters.consoleId === null ||
      game.consoles.some((c) => c.id === filters.consoleId);
    return matchesName && matchesConsole;
  });

  const fetchConsoles = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        setConsoles(await response.json());
      } else {
        console.error("Failed to fetch consoles");
      }
    } catch (error) {
      console.error("Error fetching consoles:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchGames = async () => {
    setLoading(true);
    isFetchingRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        setGames(await response.json());
      } else {
        console.error("Failed to fetch games");
      }
    } catch (error) {
      console.error("Error fetching games:", error);
    } finally {
      setLoading(false);
      isFetchingRef.current = false;
    }
  };

  const silentFetchGames = useCallback(async () => {
    if (isFetchingRef.current) return;
    isFetchingRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        setGames(await response.json());
      }
    } catch {
      // Silent fetch — don't log polling errors
    } finally {
      isFetchingRef.current = false;
    }
  }, [auth]);

  const fetchGenres = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/genres`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        setAllGenres(await response.json());
      } else {
        console.error("Failed to fetch genres");
      }
    } catch (error) {
      console.error("Error fetching genres:", error);
    } finally {
      setLoading(false);
    }
  };

  const createConsole = async (console: Console) => {
    await fetch(`${API_BASE_URL}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth?.token}`,
      },
      body: JSON.stringify(console),
    });
    fetchConsoles();
  };

  const updateConsole = async (id: number, console: Console) => {
    await fetch(`${API_BASE_URL}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth?.token}`,
      },
      body: JSON.stringify(console),
    });
    fetchConsoles();
  };

  const deleteConsole = async (id: number) => {
    await fetch(`${API_BASE_URL}/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${auth?.token}` },
    });
    fetchConsoles();
  };

  const createGame = async (payload: ConsoleGamePayload) => {
    // Changed type to payload
    const response = await fetch(`${API_BASE_URL}/games`, {
      // Added response handling
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth?.token}`,
      },
      body: JSON.stringify(payload), // Send payload
    });
    if (!response.ok) {
      // Added error handling
      throw new Error(`Failed to create game: ${response.statusText}`);
    }
    fetchGames();
    await fetchGenres(); // Refetch genres in case new ones were added
  };

  const updateGame = async (id: number, payload: ConsoleGamePayload) => {
    // Changed type to payload
    const response = await fetch(`${API_BASE_URL}/games/${id}`, {
      // Added response handling
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth?.token}`,
      },
      body: JSON.stringify(payload), // Send payload
    });
    if (!response.ok) {
      // Added error handling
      throw new Error(`Failed to update game: ${response.statusText}`);
    }
    await fetchGames(); // Ensure games list is updated
    await fetchGenres(); // Refetch genres in case new ones were added
  };

  const deleteGame = async (id: number) => {
    await fetch(`${API_BASE_URL}/games/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${auth?.token}` },
    });
    fetchGames();
  };

  useEffect(() => {
    fetchConsoles();
    fetchGames();
    fetchGenres();
  }, [auth]);

  // Polling: silently re-fetch console games every 30 seconds
  useEffect(() => {
    if (pollIntervalRef.current !== null) {
      clearInterval(pollIntervalRef.current);
    }
    pollIntervalRef.current = setInterval(() => {
      silentFetchGames();
    }, POLL_INTERVAL_MS);

    return () => {
      if (pollIntervalRef.current !== null) {
        clearInterval(pollIntervalRef.current);
        pollIntervalRef.current = null;
      }
    };
  }, [silentFetchGames]);

  const setFilters = (newFilters: Partial<FilterState>) => {
    setFiltersState((prev) => ({ ...prev, ...newFilters }));
  };

  return (
    <ConsoleContext.Provider
      value={{
        consoles,
        games,
        filteredGames,
        allGenres,
        loading,
        filters,
        fetchConsoles,
        fetchGames,
        fetchGenres,
        createConsole,
        updateConsole,
        deleteConsole,
        createGame,
        updateGame,
        deleteGame,
        setFilters,
      }}
    >
      {children}
    </ConsoleContext.Provider>
  );
};
