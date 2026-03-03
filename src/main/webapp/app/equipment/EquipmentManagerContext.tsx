import React, {
  useState,
  useEffect,
  useRef,
  useCallback,
  createContext,
  useContext,
} from "react";
import { useAuth } from "@/AuthContext";
import { EquipmentItem } from "@/types";

const API_BASE_URL = "/api";
const POLL_INTERVAL_MS = 30000;

interface SortData {
  field: keyof EquipmentItem;
  direction: "asc" | "desc";
}

interface Filters {
  name?: string;
  type?: string;
}

interface EquipmentReturnResponse {
  id: number;
  name: string;
  quantity: number;
}

interface EquipmentContextType {
  equipment: EquipmentItem[];
  loading: boolean;
  types: string[];
  filters: Filters;
  fetchEquipment: () => Promise<void>;
  fetchTypes: () => Promise<void>;
  addEquipment: (data: Partial<EquipmentItem>) => Promise<void>;
  deleteEquipment: (id: number) => Promise<void>;
  updateEquipment: (id: number, data: Partial<EquipmentItem>) => Promise<void>;
  checkout: (id: number) => Promise<void>;
  returnItem: (id: number) => Promise<void>;
  updateFiltersAndSort: (newFilters: Filters, newSort: SortData | null) => void;
  returnAllEquipment: () => Promise<EquipmentReturnResponse[] | null>;
}

export const EquipmentContext = createContext<EquipmentContextType | null>(
  null,
);

export const EquipmentProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [allEquipment, setAllEquipment] = useState<EquipmentItem[]>([]);
  const [equipment, setEquipment] = useState<EquipmentItem[]>([]);
  const [types, setTypes] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const { auth } = useAuth();
  const [filters, setFilters] = useState<Filters>({});
  const [sortData, setSortData] = useState<SortData>({
    field: "name",
    direction: "asc",
  });

  const isFetchingRef = useRef(false);
  const pollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const fetchEquipment = async () => {
    setLoading(true);
    isFetchingRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/equipment`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        const data: EquipmentItem[] = await response.json();
        setAllEquipment(data);
        applyFiltersAndSort(data);
      } else {
        console.error("Failed to fetch equipment");
      }
    } catch (error) {
      console.error("Error fetching equipment:", error);
    } finally {
      setLoading(false);
      isFetchingRef.current = false;
    }
  };

  const silentFetchEquipment = useCallback(async () => {
    if (isFetchingRef.current) return;
    isFetchingRef.current = true;
    try {
      const response = await fetch(`${API_BASE_URL}/equipment`, {
        headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
      });
      if (response.ok) {
        const data: EquipmentItem[] = await response.json();
        setAllEquipment(data);
        applyFiltersAndSort(data);
      }
    } catch {
      // Silent fetch — don't log polling errors
    } finally {
      isFetchingRef.current = false;
    }
  }, [auth]);

  const fetchTypes = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/equipment/types`);
      if (response.ok) {
        const data: string[] = await response.json();
        setTypes(data);
      } else {
        console.error("Failed to fetch equipment types");
      }
    } catch (error) {
      console.error("Error fetching equipment types:", error);
    }
  };

  const getComparableValue = (
    value: string | number | undefined,
  ): string | number | undefined => {
    return typeof value === "string" ? value.toLowerCase() : value;
  };

  const applyFiltersAndSort = (data: EquipmentItem[]) => {
    let filtered = data;

    if (filters) {
      if (filters.name) {
        filtered = filtered.filter(
          (item) =>
            item.name &&
            item.name.toLowerCase().includes(filters.name!.toLowerCase()),
        );
      }
      if (filters.type) {
        filtered = filtered.filter(
          (item) =>
            item.type &&
            item.type.toLowerCase().includes(filters.type!.toLowerCase()),
        );
      }
    }

    if (sortData) {
      const { field, direction } = sortData;
      filtered.sort((a, b) => {
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

    setEquipment(filtered);
  };

  const addEquipment = async (data: Partial<EquipmentItem>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/equipment`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(data),
      });
      if (response.ok) {
        fetchEquipment();
      } else {
        console.error("Failed to add equipment");
      }
    } catch (error) {
      console.error("Error adding equipment:", error);
    }
  };

  const deleteEquipment = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/equipment/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        fetchEquipment();
      } else {
        console.error("Failed to delete equipment");
      }
    } catch (error) {
      console.error("Error deleting equipment:", error);
    }
  };

  const checkout = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/equipment/${id}/checkout`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        setEquipment((prev) =>
          prev.map((item) =>
            item.id === id
              ? {
                  ...item,
                  availableCopies: (item.availableCopies ?? 0) - 1,
                  checkoutCount: (item.checkoutCount ?? 0) + 1,
                }
              : item,
          ),
        );
      } else {
        console.error("Failed to checkout equipment");
      }
    } catch (error) {
      console.error("Error checking out equipment:", error);
    }
  };

  const returnItem = async (id: number) => {
    try {
      const response = await fetch(`${API_BASE_URL}/equipment/${id}/return`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${auth?.token}`,
        },
      });
      if (response.ok) {
        setEquipment((prev) =>
          prev.map((item) =>
            item.id === id
              ? { ...item, availableCopies: (item.availableCopies ?? 0) + 1 }
              : item,
          ),
        );
      } else {
        console.error("Failed to return equipment");
      }
    } catch (error) {
      console.error("Error returning equipment:", error);
    }
  };

  const updateEquipment = async (id: number, data: Partial<EquipmentItem>) => {
    try {
      const response = await fetch(`${API_BASE_URL}/equipment/${id}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${auth?.token}`,
        },
        body: JSON.stringify(data),
      });
      if (response.ok) {
        fetchEquipment();
      } else {
        console.error("Failed to update equipment");
      }
    } catch (error) {
      console.error("Error updating equipment:", error);
    }
  };

  const updateFiltersAndSort = (
    newFilters: Filters,
    newSort: SortData | null,
  ) => {
    setFilters(newFilters);
    setSortData(newSort || { field: "name", direction: "asc" });
  };

  const returnAllEquipment = async (): Promise<
    EquipmentReturnResponse[] | null
  > => {
    const response = await fetch(`${API_BASE_URL}/equipment/return-all`, {
      method: "PUT",
      headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
    });
    if (response.ok) {
      const data: EquipmentReturnResponse[] = await response.json();
      return data;
    } else {
      console.error("Failed to return equipment: ", response.status);
    }
    return null;
  };

  useEffect(() => {
    fetchEquipment();
    fetchTypes();
  }, [auth]);

  useEffect(() => {
    if (allEquipment.length > 0) {
      applyFiltersAndSort(allEquipment);
    }
  }, [filters, sortData]);

  // Polling: silently re-fetch equipment every 30 seconds
  useEffect(() => {
    if (pollIntervalRef.current !== null) {
      clearInterval(pollIntervalRef.current);
    }
    pollIntervalRef.current = setInterval(() => {
      silentFetchEquipment();
    }, POLL_INTERVAL_MS);

    return () => {
      if (pollIntervalRef.current !== null) {
        clearInterval(pollIntervalRef.current);
        pollIntervalRef.current = null;
      }
    };
  }, [silentFetchEquipment]);

  return (
    <EquipmentContext.Provider
      value={{
        equipment,
        loading,
        types,
        filters,
        fetchEquipment,
        fetchTypes,
        addEquipment,
        deleteEquipment,
        updateEquipment,
        checkout,
        returnItem,
        updateFiltersAndSort,
        returnAllEquipment,
      }}
    >
      {children}
    </EquipmentContext.Provider>
  );
};

export const useEquipment = (): EquipmentContextType => {
  const context = useContext(EquipmentContext);
  if (!context) {
    throw new Error("useEquipment must be used within an EquipmentProvider");
  }
  return context;
};
