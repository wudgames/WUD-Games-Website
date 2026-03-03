import React, {
  useState,
  useEffect,
  useRef,
  useCallback,
  createContext,
  useContext,
} from "react";
import { useAuth } from "@/AuthContext";
import { Game } from "@/types";

const POLL_INTERVAL_MS = 30000;

export type { Game };

const API_BASE_URL = "/api";

interface SortData {
  field: keyof Game;
  direction: "asc" | "desc";
}

interface Filters {
  name?: string;
  genre?: string;
  playtime?: number;
  playerCount?: number;
}

interface GameManagerContextType {
  games: Game[];
  loading: boolean;
  genres: string[];
  filters: Filters;
  fetchGames: () => Promise<void>;
  fetchGenres: () => Promise<void>;
  addGame: (gameData: Partial<Game>) => Promise<void>;
  deleteGame: (gameId: number) => Promise<void>;
  updateGame: (gameId: number, gameData: Partial<Game>) => Promise<void>;
  checkout: (gameId: number) => Promise<void>;
  returnGame: (gameId: number) => Promise<void>;
  updateFiltersAndSort: (newFilters: Filters, newSort: SortData | null) => void;
  importFile: (file: File) => Promise<void>;
  exportFile: () => Promise<void>;
  fetchGameStats: (params: {
    startDate: string;
    endDate: string;
  }) => Promise<GameStats | null>;
  returnAllGames: () => Promise<GameReturnResponse[] | null>;
}

interface GameStats {
  totalCheckouts: number;
  mostPopularGameName: string;
  averageGamesCheckout: number;
  mostPopularGameNight: string;
  averagePlayersPerGame: number;
  averagePlaytimePerGame: number;
  totalAvailableCopies: number;
}

interface GameReturnResponse {
  id: number;
  name: string;
  quantity: number;
}

export const GameManagerContext = createContext<GameManagerContextType | null>(
  null,
);

export const GameManagerProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [allGames, setAllGames] = useState<Game[]>([]);
  const [games, setGames] = useState<Game[]>([]);
  const [genres, setGenres] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const { auth } = useAuth();
  const [filters, setFilters] = useState<Filters>({});
  const [sortData, setSortData] = useState<SortData>({
    field: "name",
    direction: "asc",
  });

  const isFetchingRef = useRef(false);
  const pollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const filtersRef = useRef(filters);
  const sortDataRef = useRef(sortData);

  // Keep refs in sync with state so callbacks always see current values
  useEffect(() => {
    filtersRef.current = filters;
  }, [filters]);
  useEffect(() => {
    sortDataRef.current = sortData;
  }, [sortData]);

  const fetchGames = async () => {
    setLoading(true);
    isFetchingRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        const data: Game[] = await response.json();
        setAllGames(data);
        applyFiltersAndSort(data, filtersRef.current, sortDataRef.current);
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
        const data: Game[] = await response.json();
        setAllGames(data);
        applyFiltersAndSort(data, filtersRef.current, sortDataRef.current);
      }
    } catch {
      // Silent fetch — don't log polling errors
    } finally {
      isFetchingRef.current = false;
    }
  }, [auth]);

  const fetchGenres = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/genres`);
      if (response.ok) {
        const data: string[] = await response.json();
        setGenres(data);
      } else {
        console.error("Failed to fetch genres");
      }
    } catch (error) {
      console.error("Error fetching genres:", error);
    }
  };

  const getComparableValue = (
    value: string | number | undefined,
  ): string | number | undefined => {
    return typeof value === "string" ? value.toLowerCase() : value;
  };

  const applyFiltersAndSort = (
    data: Game[],
    currentFilters: Filters,
    currentSort: SortData,
  ) => {
    let filteredGames = [...data];

    // Apply filtering based on the Spring query logic
    if (currentFilters) {
      if (currentFilters.name) {
        filteredGames = filteredGames.filter(
          (game) =>
            game.name &&
            game.name
              .toLowerCase()
              .includes(currentFilters.name!.toLowerCase()),
        );
      }
      if (currentFilters.genre) {
        filteredGames = filteredGames.filter(
          (game) =>
            game.genre &&
            game.genre
              .toLowerCase()
              .includes(currentFilters.genre!.toLowerCase()),
        );
      }
      if (currentFilters.playtime !== undefined) {
        filteredGames = filteredGames.filter(
          (game) =>
            game.minPlaytime !== undefined &&
            game.maxPlaytime !== undefined &&
            game.minPlaytime <= currentFilters.playtime! &&
            game.maxPlaytime >= currentFilters.playtime!,
        );
      }
      if (currentFilters.playerCount !== undefined) {
        filteredGames = filteredGames.filter(
          (game) =>
            game.minPlayerCount !== undefined &&
            game.maxPlayerCount !== undefined &&
            game.minPlayerCount <= currentFilters.playerCount! &&
            game.maxPlayerCount >= currentFilters.playerCount!,
        );
      }
    }

    // Apply client-side sorting
    if (currentSort) {
      const { field, direction } = currentSort;
      filteredGames.sort((a, b) => {
        const valueA = a[field];
        const valueB = b[field];

        if (valueA === undefined) return 1;
        if (valueB === undefined) return -1;

        const compA = getComparableValue(valueA);
        const compB = getComparableValue(valueB);

        if (compA === undefined) return 1;
        if (compB === undefined) return -1;

        if (compA < compB) return direction === "asc" ? -1 : 1;
        if (compA > compB) return direction === "asc" ? 1 : -1;
        return 0;
      });
    }

    setGames(filteredGames);
  };

  const addGame = async (gameData: Partial<Game>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(gameData),
      });
      if (response.ok) {
        fetchGames();
      } else {
        console.error("Failed to add game");
      }
    } catch (error) {
      console.error("Error adding game:", error);
    }
  };

  const deleteGame = async (gameId: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/${gameId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        fetchGames();
      } else {
        console.error("Failed to delete game");
      }
    } catch (error) {
      console.error("Error deleting game:", error);
    }
  };
  const checkout = async (gameId: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/${gameId}/checkout`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        const updater = (prev: Game[]) =>
          prev.map((game) =>
            game.id === gameId
              ? {
                  ...game,
                  availableCopies: (game.availableCopies ?? 0) - 1,
                  checkoutCount: (game.checkoutCount ?? 0) + 1,
                }
              : game,
          );
        setAllGames(updater);
        setGames(updater);
      } else {
        console.error("Failed to checkout game");
      }
    } catch (error) {
      console.error("Error checkingout game:", error);
    }
  };
  const returnGame = async (gameId: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/${gameId}/return`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        const updater = (prev: Game[]) =>
          prev.map((game) =>
            game.id === gameId
              ? { ...game, availableCopies: (game.availableCopies ?? 0) + 1 }
              : game,
          );
        setAllGames(updater);
        setGames(updater);
      } else {
        console.error("Failed to checkout game");
      }
    } catch (error) {
      console.error("Error checkingout game:", error);
    }
  };
  const updateGame = async (gameId: number, gameData: Partial<Game>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/${gameId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(gameData),
      });
      if (response.ok) {
        fetchGames();
      } else {
        console.error("Failed to update game");
      }
    } catch (error) {
      console.error("Error update game:", error);
    }
  };

  const updateFiltersAndSort = (
    newFilters: Filters,
    newSort: SortData | null,
  ) => {
    setFilters(newFilters);
    setSortData(newSort || { field: "name", direction: "desc" }); // Keep default sort if none is provided
  };
  const importFile = async (file: File) => {
    setLoading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      await fetch(`${API_BASE_URL}/games/import`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
        body: formData,
      });

      fetchGames();
    } catch (error) {
      console.error("Error updating filters and sort:", error);
    }
  };

  const exportFile = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/download-csv`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });

      if (!response.ok) {
        throw new Error("Network response was not ok");
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "boardgames.csv";
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("There was an error downloading the CSV:", error);
    }
  };
  const fetchGameStats = async ({
    startDate,
    endDate,
  }: {
    startDate: string;
    endDate: string;
  }): Promise<GameStats | null> => {
    const params = new URLSearchParams();
    if (startDate) params.append("startDate", startDate);
    if (endDate) params.append("endDate", endDate);

    try {
      const response = await fetch(
        `${API_BASE_URL}/games/stats?${params.toString()}`,
        {
          headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
        },
      );
      if (response.ok) {
        const data: GameStats = await response.json();
        return data;
      } else {
        console.error("Failed to fetch game stats");
      }
    } catch (error) {
      console.error("Error fetching game stats:", error);
    }
    return null;
  };

  const returnAllGames = async (): Promise<GameReturnResponse[] | null> => {
    const response = await fetch(`${API_BASE_URL}/games/return-all`, {
      method: "PUT",
      headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
    });
    if (response.ok) {
      const data: GameReturnResponse[] = await response.json();
      return data;
    } else {
      console.error("Failed to return games: ", response.status);
    }

    return null;
  };

  useEffect(() => {
    fetchGames();
    fetchGenres();
  }, [auth]);

  useEffect(() => {
    if (allGames.length > 0) {
      applyFiltersAndSort(allGames, filters, sortData);
    }
  }, [filters, sortData]);

  // Polling: silently re-fetch games every 30 seconds
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

  return (
    <GameManagerContext.Provider
      value={{
        games,
        loading,
        genres,
        filters,
        fetchGames,
        fetchGenres,
        addGame,
        deleteGame,
        updateGame,
        checkout,
        returnGame,
        updateFiltersAndSort,
        importFile,
        exportFile,
        fetchGameStats,
        returnAllGames,
      }}
    >
      {children}
    </GameManagerContext.Provider>
  );
};

export const useGameManager = (): GameManagerContextType => {
  const context = useContext(GameManagerContext);
  if (!context) {
    throw new Error("useGameManager must be used within a GameManagerProvider");
  }
  return context;
};
