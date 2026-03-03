import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Loader2 } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { SteamGame } from "@/types";

interface SteamSearchResult {
  name: string;
  appId: number;
}

interface SteamSearchDetails {
  name: string;
  description: string;
  headerImage: string;
  windows: boolean;
  mac: boolean;
  linux: boolean;
}

interface SteamSearchDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (game: Partial<SteamGame>) => void;
}

const SteamSearchDialog: React.FC<SteamSearchDialogProps> = ({
  isOpen,
  onClose,
  onSelect,
}) => {
  const { auth } = useAuth();
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<SteamSearchResult[]>([]);
  const [searching, setSearching] = useState(false);
  const [loadingDetails, setLoadingDetails] = useState(false);

  useEffect(() => {
    if (!isOpen) {
      setQuery("");
      setResults([]);
    }
  }, [isOpen]);

  const handleSearch = async () => {
    if (!query.trim()) return;
    setSearching(true);
    try {
      const response = await fetch(
        `/api/consoles/steam/search?name=${encodeURIComponent(query.trim())}`,
        {
          headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
        },
      );
      if (response.ok) {
        const data: SteamSearchResult[] = await response.json();
        setResults(data);
      } else {
        console.error("Steam search failed");
      }
    } catch (error) {
      console.error("Error searching Steam:", error);
    } finally {
      setSearching(false);
    }
  };

  const handleSelect = async (result: SteamSearchResult) => {
    setLoadingDetails(true);
    try {
      const response = await fetch(
        `/api/consoles/steam/details?appId=${result.appId}`,
        {
          headers: auth ? { Authorization: `Bearer ${auth.token}` } : {},
        },
      );
      if (response.ok) {
        const details: SteamSearchDetails = await response.json();
        onSelect({
          name: details.name || result.name,
          description: details.description,
          steamAppId: String(result.appId),
          imageUrl: details.headerImage,
          windows: details.windows,
          macos: details.mac,
          linux: details.linux,
        });
      } else {
        onSelect({
          name: result.name,
          steamAppId: String(result.appId),
        });
      }
    } catch {
      onSelect({
        name: result.name,
        steamAppId: String(result.appId),
      });
    } finally {
      setLoadingDetails(false);
      onClose();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      void handleSearch();
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-lg max-h-[80vh] flex flex-col">
        <DialogHeader>
          <DialogTitle>Search Steam Store</DialogTitle>
          <DialogDescription>
            Search for a game on Steam and auto-fill its details.
          </DialogDescription>
        </DialogHeader>

        <div className="flex gap-2">
          <Input
            placeholder="Search Steam..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <Button onClick={() => void handleSearch()} disabled={searching}>
            {searching ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <Search className="h-4 w-4" />
            )}
          </Button>
        </div>

        <div className="overflow-y-auto flex-1 space-y-2">
          {loadingDetails && (
            <div className="flex items-center justify-center py-4">
              <Loader2 className="h-6 w-6 animate-spin" />
              <span className="ml-2">Loading game details...</span>
            </div>
          )}
          {!loadingDetails &&
            results.map((result) => (
              <Card
                key={result.appId}
                className="cursor-pointer hover:bg-accent/50 hover:border-primary/30 transition-colors"
                onClick={() => void handleSelect(result)}
              >
                <CardContent className="p-3">
                  <p className="font-medium">{result.name}</p>
                  <p className="text-xs text-muted-foreground">
                    App ID: {result.appId}
                  </p>
                </CardContent>
              </Card>
            ))}
          {!loadingDetails && results.length === 0 && query && !searching && (
            <p className="text-center text-muted-foreground py-4">
              No results found. Try a different search term.
            </p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default SteamSearchDialog;
