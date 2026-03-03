import React, {
  useState,
  useEffect,
  useRef,
  useCallback,
  createContext,
  useContext,
} from "react";
import { useAuth } from "@/AuthContext";
import { SteamGame, SteamAccount, SteamAccountRequest } from "@/types";

const API_BASE_URL = "/api/steam";
const POLL_INTERVAL_MS = 30000;

interface SteamContextType {
  steamGames: SteamGame[];
  steamAccounts: SteamAccount[];
  requests: SteamAccountRequest[];
  loadingGames: boolean;
  loadingAccounts: boolean;
  loadingRequests: boolean;
  fetchSteamGames: () => Promise<void>;
  addSteamGame: (data: Partial<SteamGame>) => Promise<void>;
  updateSteamGame: (id: number, data: Partial<SteamGame>) => Promise<void>;
  deleteSteamGame: (id: number) => Promise<void>;
  fetchSteamAccounts: () => Promise<void>;
  addSteamAccount: (data: Partial<SteamAccount>) => Promise<void>;
  updateSteamAccount: (
    id: number,
    data: Partial<SteamAccount>,
  ) => Promise<void>;
  deleteSteamAccount: (id: number) => Promise<void>;
  fetchRequests: (status?: string) => Promise<void>;
  submitRequest: (data: {
    name: string;
    email: string;
    gameName: string;
    comments?: string;
    rentalStartDay?: string;
    rentalEndDay?: string;
  }) => Promise<void>;
  approveRequest: (id: number, accountId: number) => Promise<void>;
  denyRequest: (id: number) => Promise<void>;
  returnRequest: (id: number) => Promise<void>;
}

export const SteamContext = createContext<SteamContextType | null>(null);

export const SteamProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [steamGames, setSteamGames] = useState<SteamGame[]>([]);
  const [steamAccounts, setSteamAccounts] = useState<SteamAccount[]>([]);
  const [requests, setRequests] = useState<SteamAccountRequest[]>([]);
  const [loadingGames, setLoadingGames] = useState(true);
  const [loadingAccounts, setLoadingAccounts] = useState(true);
  const [loadingRequests, setLoadingRequests] = useState(true);
  const { auth } = useAuth();

  const isFetchingGamesRef = useRef(false);
  const isFetchingAccountsRef = useRef(false);
  const isFetchingRequestsRef = useRef(false);
  const pollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // --- Steam Games ---

  const fetchSteamGames = async () => {
    setLoadingGames(true);
    isFetchingGamesRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        const data: SteamGame[] = await response.json();
        setSteamGames(data);
      } else {
        console.error("Failed to fetch steam games");
      }
    } catch (error) {
      console.error("Error fetching steam games:", error);
    } finally {
      setLoadingGames(false);
      isFetchingGamesRef.current = false;
    }
  };

  const silentFetchGames = useCallback(async () => {
    if (isFetchingGamesRef.current) return;
    isFetchingGamesRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        const data: SteamGame[] = await response.json();
        setSteamGames(data);
      }
    } catch {
      // Silent fetch — don't log polling errors
    } finally {
      isFetchingGamesRef.current = false;
    }
  }, [auth]);

  const addSteamGame = async (data: Partial<SteamGame>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(data),
      });
      if (response.ok) {
        await fetchSteamGames();
      } else {
        console.error("Failed to add steam game");
      }
    } catch (error) {
      console.error("Error adding steam game:", error);
    }
  };

  const updateSteamGame = async (id: number, data: Partial<SteamGame>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(data),
      });
      if (response.ok) {
        await fetchSteamGames();
      } else {
        console.error("Failed to update steam game");
      }
    } catch (error) {
      console.error("Error updating steam game:", error);
    }
  };

  const deleteSteamGame = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/games/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        await fetchSteamGames();
      } else {
        console.error("Failed to delete steam game");
      }
    } catch (error) {
      console.error("Error deleting steam game:", error);
    }
  };

  // --- Steam Accounts ---

  const fetchSteamAccounts = async () => {
    if (!auth) {
      setSteamAccounts([]);
      setLoadingAccounts(false);
      return;
    }
    setLoadingAccounts(true);
    isFetchingAccountsRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/accounts`, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      if (response.ok) {
        const data: SteamAccount[] = await response.json();
        setSteamAccounts(data);
      } else {
        console.error("Failed to fetch steam accounts");
      }
    } catch (error) {
      console.error("Error fetching steam accounts:", error);
    } finally {
      setLoadingAccounts(false);
      isFetchingAccountsRef.current = false;
    }
  };

  const silentFetchAccounts = useCallback(async () => {
    if (!auth || isFetchingAccountsRef.current) return;
    isFetchingAccountsRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/accounts`, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      if (response.ok) {
        const data: SteamAccount[] = await response.json();
        setSteamAccounts(data);
      }
    } catch {
      // Silent fetch
    } finally {
      isFetchingAccountsRef.current = false;
    }
  }, [auth]);

  const addSteamAccount = async (data: Partial<SteamAccount>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/accounts`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(data),
      });
      if (response.ok) {
        await fetchSteamAccounts();
      } else {
        console.error("Failed to add steam account");
      }
    } catch (error) {
      console.error("Error adding steam account:", error);
    }
  };

  const updateSteamAccount = async (
    id: number,
    data: Partial<SteamAccount>,
  ) => {
    try {
      const response = await fetch(`${API_BASE_URL}/accounts/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(data),
      });
      if (response.ok) {
        await fetchSteamAccounts();
      } else {
        console.error("Failed to update steam account");
      }
    } catch (error) {
      console.error("Error updating steam account:", error);
    }
  };

  const deleteSteamAccount = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/accounts/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        await fetchSteamAccounts();
      } else {
        console.error("Failed to delete steam account");
      }
    } catch (error) {
      console.error("Error deleting steam account:", error);
    }
  };

  // --- Account Requests ---

  const fetchRequests = async (status?: string) => {
    setLoadingRequests(true);
    isFetchingRequestsRef.current = true;
    try {
      const url = status
        ? `${API_BASE_URL}/requests?status=${encodeURIComponent(status)}`
        : `${API_BASE_URL}/requests`;
      const response = await fetch(url, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        const data: SteamAccountRequest[] = await response.json();
        setRequests(data);
      } else {
        // Non-authorized users may get 403 — just clear requests
        setRequests([]);
      }
    } catch (error) {
      console.error("Error fetching requests:", error);
    } finally {
      setLoadingRequests(false);
      isFetchingRequestsRef.current = false;
    }
  };

  const silentFetchRequests = useCallback(async () => {
    if (!auth || isFetchingRequestsRef.current) return;
    isFetchingRequestsRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/requests`, {
        headers: { Authorization: `Bearer ${auth.token}` },
      });
      if (response.ok) {
        const data: SteamAccountRequest[] = await response.json();
        setRequests(data);
      }
    } catch {
      // Silent fetch
    } finally {
      isFetchingRequestsRef.current = false;
    }
  }, [auth]);

  const submitRequest = async (data: {
    name: string;
    email: string;
    gameName: string;
    comments?: string;
    rentalStartDay?: string;
    rentalEndDay?: string;
  }) => {
    const response = await fetch(`${API_BASE_URL}/requests`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(auth ? { Authorization: `Bearer ${auth.token}` } : {}),
      },
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      throw new Error("Failed to submit request");
    }
    await fetchRequests();
  };

  const approveRequest = async (id: number, accountId: number) => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/requests/${id}/approve?accountId=${accountId}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${auth?.token}`,
          },
        },
      );
      if (response.ok) {
        await fetchRequests();
        await fetchSteamAccounts();
      } else {
        console.error("Failed to approve request");
      }
    } catch (error) {
      console.error("Error approving request:", error);
    }
  };

  const denyRequest = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/requests/${id}/deny`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        await fetchRequests();
      } else {
        console.error("Failed to deny request");
      }
    } catch (error) {
      console.error("Error denying request:", error);
    }
  };

  const returnRequest = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/requests/${id}/return`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        await fetchRequests();
        await fetchSteamAccounts();
      } else {
        console.error("Failed to return request");
      }
    } catch (error) {
      console.error("Error returning request:", error);
    }
  };

  // Initial data loading
  useEffect(() => {
    void fetchSteamGames();
    if (auth) {
      const isAdmin = auth.authenticationLevel.toLowerCase() === "admin";
      const isHost =
        isAdmin || auth.authenticationLevel.toLowerCase() === "host";
      if (isAdmin) {
        void fetchSteamAccounts();
      }
      if (isHost) {
        void fetchRequests();
      }
    } else {
      setSteamAccounts([]);
      setLoadingAccounts(false);
      setRequests([]);
      setLoadingRequests(false);
    }
  }, [auth]);

  // Polling
  useEffect(() => {
    if (pollIntervalRef.current !== null) {
      clearInterval(pollIntervalRef.current);
    }
    pollIntervalRef.current = setInterval(() => {
      void silentFetchGames();
      void silentFetchAccounts();
      void silentFetchRequests();
    }, POLL_INTERVAL_MS);

    return () => {
      if (pollIntervalRef.current !== null) {
        clearInterval(pollIntervalRef.current);
        pollIntervalRef.current = null;
      }
    };
  }, [silentFetchGames, silentFetchAccounts, silentFetchRequests]);

  return (
    <SteamContext.Provider
      value={{
        steamGames,
        steamAccounts,
        requests,
        loadingGames,
        loadingAccounts,
        loadingRequests,
        fetchSteamGames,
        addSteamGame,
        updateSteamGame,
        deleteSteamGame,
        fetchSteamAccounts,
        addSteamAccount,
        updateSteamAccount,
        deleteSteamAccount,
        fetchRequests,
        submitRequest,
        approveRequest,
        denyRequest,
        returnRequest,
      }}
    >
      {children}
    </SteamContext.Provider>
  );
};

export const useSteam = (): SteamContextType => {
  const context = useContext(SteamContext);
  if (!context) {
    throw new Error("useSteam must be used within a SteamProvider");
  }
  return context;
};
