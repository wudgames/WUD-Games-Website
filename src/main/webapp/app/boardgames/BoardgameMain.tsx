import React, { useState, useEffect, useCallback } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from "@/components/ui/dialog";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Pencil,
  Trash2,
  Plus,
  Minus,
  BarChart,
  RefreshCw,
  Filter,
  Upload,
  Download,
  X,
  MapPin,
  Users,
  Clock,
  Tag,
  Dice6,
} from "lucide-react";
import { Game, useGameManager } from "./GameManagerContext";
import { useAuth } from "@/AuthContext";

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { AddGamePopup, EditGamePopup } from "@/boardgames/GamePopups";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";

import PageHeader from "@/components/PageHeader";

// --- Similar Games Component ---

interface SimilarGamesProps {
  gameId: number;
  onSelectGame: (game: Game) => void;
}

const SimilarGames: React.FC<SimilarGamesProps> = ({
  gameId,
  onSelectGame,
}) => {
  const [similarGames, setSimilarGames] = useState<Game[]>([]);
  const [loading, setLoading] = useState(true);
  const { auth } = useAuth();

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setSimilarGames([]);

    const fetchSimilar = async () => {
      try {
        const headers: Record<string, string> = {};
        if (auth?.token) {
          headers["Authorization"] = `Bearer ${auth.token}`;
        }
        const response = await fetch(`/api/games/${gameId}/similar`, {
          headers,
        });
        if (response.ok && !cancelled) {
          const data: Game[] = await response.json();
          setSimilarGames(data);
        }
      } catch (error) {
        console.error("Error fetching similar games:", error);
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    void fetchSimilar();
    return () => {
      cancelled = true;
    };
  }, [gameId, auth?.token]);

  if (!loading && similarGames.length === 0) {
    return null;
  }

  return (
    <div className="mt-4 pt-4 border-t">
      <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide mb-3">
        Similar Games
      </h4>
      <div className="flex gap-3 overflow-x-auto pb-2">
        {loading
          ? Array.from({ length: 3 }).map((_, i) => (
              <div
                key={i}
                className="flex-shrink-0 w-28 rounded-lg border bg-card p-2"
              >
                <Skeleton className="w-full h-20 rounded" />
                <Skeleton className="h-3 w-20 mt-2" />
              </div>
            ))
          : similarGames.map((similar) => (
              <button
                key={similar.id}
                type="button"
                className="flex-shrink-0 w-28 rounded-lg border bg-card p-2 text-left hover:bg-accent hover:border-primary/30 transition-colors cursor-pointer"
                onClick={() => onSelectGame(similar)}
              >
                {similar.boxImageUrl ? (
                  <img
                    src={similar.boxImageUrl}
                    alt={similar.name}
                    className="w-full h-20 object-contain rounded"
                  />
                ) : (
                  <div className="w-full h-20 rounded bg-muted flex items-center justify-center text-xs text-muted-foreground">
                    No image
                  </div>
                )}
                <p className="mt-1 text-xs font-medium truncate">
                  {similar.name}
                </p>
              </button>
            ))}
      </div>
    </div>
  );
};

// --- Game Card Component ---

interface GameCardProps {
  game: Game;
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

const GameCard: React.FC<GameCardProps> = ({ game }) => {
  const { auth } = useAuth();
  const { deleteGame, checkout, returnGame, updateGame } = useGameManager();
  const [editingGame, setEditingGame] = useState<Game | null>(null);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showInfoDialog, setShowInfoDialog] = useState(false);
  const [viewedGame, setViewedGame] = useState<Game>(game);
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";
  const isHost = isAdmin || auth?.authenticationLevel.toLowerCase() === "host";

  const openInfoDialog = useCallback(() => {
    setViewedGame(game);
    setShowInfoDialog(true);
  }, [game]);

  const handleSelectSimilarGame = useCallback((similar: Game) => {
    setViewedGame(similar);
  }, []);

  const handleEditGame = async (updatedGame: Partial<Game>) => {
    if (!editingGame) return;
    await updateGame(game.id, updatedGame);
    setEditingGame(null);
  };

  const handleDelete = async () => {
    deleteGame(game.id);
  };

  const handleCheckout = async () => {
    checkout(game.id);
  };

  const handleReturn = async () => {
    returnGame(game.id);
  };

  const isReturnDisabled = game.availableCopies === game.quantity;
  const isCheckoutDisabled =
    game.availableCopies === null ||
    game.availableCopies === undefined ||
    game.availableCopies <= 0;

  const availabilityColor =
    game.availableCopies === 0
      ? "text-destructive"
      : (game.availableCopies ?? 0) < (game.quantity ?? 0)
        ? "text-warning"
        : "text-success";

  return (
    <>
      <Card
        className={`w-full relative flex flex-col overflow-hidden hover:shadow-md hover:border-primary/30 transition-all duration-200 cursor-pointer group ${isHost ? "pb-12" : ""}`}
        onClick={() => openInfoDialog()}
      >
        {/* Image Section */}
        <div className="relative w-full aspect-[3/2] bg-muted overflow-hidden">
          {game.boxImageUrl ? (
            <img
              src={game.boxImageUrl}
              alt={game.name}
              className="w-full h-full object-contain p-2 group-hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <Dice6 className="w-12 h-12 text-muted-foreground/30" />
            </div>
          )}
          {/* Availability badge */}
          <div
            className={`absolute top-2 right-2 text-xs font-semibold px-2 py-0.5 rounded-full ${
              game.availableCopies === 0
                ? "bg-destructive/10 text-destructive"
                : "bg-success/10 text-success"
            }`}
          >
            {game.availableCopies}/{game.quantity}
          </div>
        </div>

        {/* Content Section */}
        <CardContent className="p-3 flex-1">
          <h3 className="font-semibold text-sm leading-tight line-clamp-2 mb-2">
            {game.name}
          </h3>
          <div className="space-y-1 text-xs text-muted-foreground">
            <div className="flex items-center gap-1.5">
              <Users className="w-3 h-3 shrink-0" />
              <span>
                {game.minPlayerCount}
                {game.minPlayerCount !== game.maxPlayerCount &&
                  `-${game.maxPlayerCount}`}{" "}
                players
              </span>
            </div>
            <div className="flex items-center gap-1.5">
              <Clock className="w-3 h-3 shrink-0" />
              <span>
                {game.minPlaytime}
                {game.minPlaytime !== game.maxPlaytime &&
                  `-${game.maxPlaytime}`}{" "}
                min
              </span>
            </div>
            {game.genre && (
              <div className="flex items-center gap-1.5">
                <Tag className="w-3 h-3 shrink-0" />
                <span className="truncate">{game.genre}</span>
              </div>
            )}
            {game.location && (
              <div className="flex items-center gap-1.5">
                <MapPin className="w-3 h-3 shrink-0" />
                <span className="truncate">{game.location}</span>
              </div>
            )}
          </div>
        </CardContent>

        {/* Action Buttons */}
        {isHost && (
          <div className="absolute bottom-0 left-0 right-0 px-2 py-1.5 border-t bg-card/80 backdrop-blur-sm flex justify-between items-center">
            {isAdmin && (
              <div className="flex gap-1">
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8"
                  onClick={(e) => {
                    e.stopPropagation();
                    setEditingGame(game);
                  }}
                >
                  <Pencil className="w-3.5 h-3.5" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 text-destructive hover:text-destructive"
                  onClick={(e) => {
                    e.stopPropagation();
                    setShowDeleteDialog(true);
                  }}
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </Button>
              </div>
            )}
            <div
              className={`flex gap-1 ${!isAdmin ? "ml-auto" : ""}`}
              onClick={(e) => e.stopPropagation()}
            >
              <Button
                title="Checkout Game (remove a copy)"
                variant="outline"
                size="sm"
                className="h-8 px-2 text-destructive border-destructive/30 hover:bg-destructive/10"
                onClick={() => {
                  handleCheckout();
                }}
                disabled={isCheckoutDisabled}
              >
                <Minus className="w-3.5 h-3.5 mr-1" />
                Out
              </Button>
              <Button
                title="Return Game (add a copy back)"
                variant="outline"
                size="sm"
                className="h-8 px-2 text-success border-success/30 hover:bg-success/10"
                onClick={() => {
                  handleReturn();
                }}
                disabled={isReturnDisabled}
              >
                <Plus className="w-3.5 h-3.5 mr-1" />
                In
              </Button>
            </div>
          </div>
        )}
      </Card>

      {/* Game Info Dialog */}
      <Dialog open={showInfoDialog} onOpenChange={setShowInfoDialog}>
        <DialogContent className="max-w-2xl max-h-[90vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="text-2xl">{viewedGame.name}</DialogTitle>
            <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
              <span className="flex items-center gap-1">
                <Users className="w-3.5 h-3.5" />
                {viewedGame.minPlayerCount}
                {viewedGame.minPlayerCount !== viewedGame.maxPlayerCount &&
                  `-${viewedGame.maxPlayerCount}`}{" "}
                players
              </span>
              <span className="flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" />
                {viewedGame.minPlaytime}
                {viewedGame.minPlaytime !== viewedGame.maxPlaytime &&
                  `-${viewedGame.maxPlaytime}`}{" "}
                min
              </span>
              {viewedGame.genre && (
                <span className="flex items-center gap-1">
                  <Tag className="w-3.5 h-3.5" />
                  {viewedGame.genre}
                </span>
              )}
            </div>
          </DialogHeader>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 overflow-y-auto">
            <div className="md:col-span-2 space-y-4">
              <div>
                <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                  Description
                </h4>
                <p className="mt-1 text-sm">
                  {viewedGame.description || "No description available."}
                </p>
              </div>
              {isHost && (
                <>
                  {viewedGame.internalNotes && (
                    <div>
                      <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                        Internal Notes
                      </h4>
                      <p className="mt-1 text-sm italic">
                        {viewedGame.internalNotes}
                      </p>
                    </div>
                  )}
                  <div className="grid grid-cols-2 gap-4">
                    <div className="rounded-lg bg-muted/50 p-3">
                      <p className="text-xs text-muted-foreground uppercase tracking-wide">
                        Availability
                      </p>
                      <p className={`text-lg font-bold ${availabilityColor}`}>
                        {viewedGame.availableCopies} / {viewedGame.quantity}
                      </p>
                    </div>
                    <div className="rounded-lg bg-muted/50 p-3">
                      <p className="text-xs text-muted-foreground uppercase tracking-wide">
                        Times Checked Out
                      </p>
                      <p className="text-lg font-bold">
                        {viewedGame.checkoutCount}
                      </p>
                    </div>
                  </div>
                </>
              )}
              {viewedGame.location && (
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <MapPin className="w-4 h-4" />
                  {viewedGame.location}
                </div>
              )}
            </div>
            <div className="flex justify-center">
              {viewedGame.boxImageUrl && (
                <img
                  src={viewedGame.boxImageUrl}
                  alt={viewedGame.name}
                  className="w-full max-h-64 object-contain rounded-lg"
                />
              )}
            </div>
          </div>
          <SimilarGames
            gameId={viewedGame.id}
            onSelectGame={handleSelectSimilarGame}
          />
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Close</Button>
            </DialogClose>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <EditGamePopup
        game={editingGame!}
        onSubmit={handleEditGame}
        onClose={() => setEditingGame(null)}
        isOpen={Boolean(editingGame)}
      />

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Game</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete &quot;{game.name}&quot;? This
              action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
};

// --- Stats Popup ---

interface StatsPopupProps {
  isOpen: boolean;
  onClose: (open: boolean) => void;
}

export const StatsPopup: React.FC<StatsPopupProps> = ({ isOpen, onClose }) => {
  const [stats, setStats] = useState<GameStats | null>(null);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const { fetchGameStats } = useGameManager();

  useEffect(() => {
    if (isOpen) {
      setStats(null);
      (async () => {
        const data = await fetchGameStats({ startDate, endDate });
        setStats(data);
      })();
    }
  }, [isOpen, startDate, endDate, fetchGameStats]);

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name === "startDate") {
      setStartDate(value);
    } else if (name === "endDate") {
      setEndDate(value);
    }
  };

  if (!isOpen) return null;

  const statCards = stats
    ? [
        { label: "Total Checkouts", value: stats.totalCheckouts },
        { label: "Most Popular Game", value: stats.mostPopularGameName },
        {
          label: "Avg Games/Checkout",
          value: stats.averageGamesCheckout.toFixed(1),
        },
        { label: "Busiest Night", value: stats.mostPopularGameNight },
        {
          label: "Avg Players/Game",
          value: stats.averagePlayersPerGame.toFixed(1),
        },
        {
          label: "Avg Playtime",
          value: `${stats.averagePlaytimePerGame.toFixed(0)} min`,
        },
        { label: "Total Copies", value: stats.totalAvailableCopies },
      ]
    : [];

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Library Statistics</DialogTitle>
          <DialogDescription>
            Game checkout statistics for the selected date range.
          </DialogDescription>
        </DialogHeader>
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div>
            <Label className="text-xs">Start Date</Label>
            <Input
              type="date"
              name="startDate"
              value={startDate}
              onChange={handleDateChange}
              className="mt-1"
            />
          </div>
          <div>
            <Label className="text-xs">End Date</Label>
            <Input
              type="date"
              name="endDate"
              value={endDate}
              onChange={handleDateChange}
              className="mt-1"
            />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-3">
          {stats
            ? statCards.map((stat) => (
                <div
                  key={stat.label}
                  className="rounded-lg bg-muted/50 p-3 text-center"
                >
                  <p className="text-xs text-muted-foreground uppercase tracking-wide">
                    {stat.label}
                  </p>
                  <p className="text-lg font-bold mt-1">{stat.value}</p>
                </div>
              ))
            : Array.from({ length: 7 }).map((_, i) => (
                <div key={i} className="rounded-lg bg-muted/50 p-3 text-center">
                  <Skeleton className="h-3 w-20 mx-auto" />
                  <Skeleton className="h-6 w-12 mx-auto mt-2" />
                </div>
              ))}
        </div>
      </DialogContent>
    </Dialog>
  );
};

// --- Inline Filters ---

interface InlineFilterState {
  name: string;
  genre: string;
  playtime: number | undefined;
  playerCount: number | undefined;
}

const InlineFilters = () => {
  const [isVisible, setIsVisible] = useState(false);
  const [filters, setFilters] = useState<InlineFilterState>({
    name: "",
    genre: "",
    playtime: undefined,
    playerCount: undefined,
  });
  const [sortField, setSortField] = useState("name");
  const [sortDirection, setSortDirection] = useState("asc");

  const { updateFiltersAndSort, genres } = useGameManager();

  useEffect(() => {
    updateFiltersAndSort(filters, {
      field: sortField as keyof Game,
      direction: sortDirection as "asc" | "desc",
    });
  }, [filters, sortField, sortDirection]);

  const handleClear = () => {
    setFilters({
      name: "",
      genre: "",
      playtime: undefined,
      playerCount: undefined,
    });
    setSortField("name");
    setSortDirection("asc");
  };

  const hasActiveFilters =
    filters.name ||
    filters.genre ||
    filters.playtime !== undefined ||
    filters.playerCount !== undefined ||
    sortField !== "name" ||
    sortDirection !== "asc";

  return (
    <div className="space-y-3">
      <Button
        variant={hasActiveFilters ? "default" : "outline"}
        size="sm"
        onClick={() => setIsVisible(!isVisible)}
        className="flex items-center gap-2"
      >
        <Filter className="w-4 h-4" />
        Filter
        {hasActiveFilters && (
          <span className="ml-1 rounded-full bg-primary-foreground text-primary w-2 h-2" />
        )}
      </Button>

      <div
        className={`transition-all duration-300 ease-in-out overflow-hidden ${
          isVisible ? "max-h-[500px] opacity-100" : "max-h-0 opacity-0"
        }`}
      >
        <div className="rounded-lg border bg-card p-4 shadow-sm">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <Label className="text-xs">Name</Label>
              <Input
                type="text"
                value={filters.name}
                onChange={(e) =>
                  setFilters({ ...filters, name: e.target.value })
                }
                placeholder="Search by name..."
              />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Genre</Label>
              <Input
                type="text"
                list="genre-suggestions"
                value={filters.genre}
                onChange={(e) =>
                  setFilters({ ...filters, genre: e.target.value })
                }
                placeholder="Filter by genre..."
              />
              <datalist id="genre-suggestions">
                {genres.map((genre) => (
                  <option key={genre} value={genre} />
                ))}
              </datalist>
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Playtime (min)</Label>
              <Input
                type="number"
                value={filters.playtime ?? ""}
                onChange={(e) =>
                  setFilters({
                    ...filters,
                    playtime: e.target.value
                      ? parseInt(e.target.value, 10)
                      : undefined,
                  })
                }
                placeholder="Max playtime..."
              />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Player Count</Label>
              <Input
                type="number"
                value={filters.playerCount ?? ""}
                onChange={(e) =>
                  setFilters({
                    ...filters,
                    playerCount: e.target.value
                      ? parseInt(e.target.value, 10)
                      : undefined,
                  })
                }
                placeholder="Number of players..."
              />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Sort By</Label>
              <Select value={sortField} onValueChange={setSortField}>
                <SelectTrigger>
                  <SelectValue placeholder="Sort by" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="name">Name</SelectItem>
                  <SelectItem value="minPlayerCount">Min Players</SelectItem>
                  <SelectItem value="maxPlayerCount">Max Players</SelectItem>
                  <SelectItem value="minPlaytime">Min Playtime</SelectItem>
                  <SelectItem value="maxPlaytime">Max Playtime</SelectItem>
                  <SelectItem value="checkoutCount">Popularity</SelectItem>
                  <SelectItem value="createdAt">Date Added</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Order</Label>
              <div className="flex gap-2">
                <Select value={sortDirection} onValueChange={setSortDirection}>
                  <SelectTrigger className="flex-1">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="asc">Ascending</SelectItem>
                    <SelectItem value="desc">Descending</SelectItem>
                  </SelectContent>
                </Select>
                {hasActiveFilters && (
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={handleClear}
                    title="Clear all filters"
                    className="shrink-0"
                  >
                    <X className="w-4 h-4" />
                  </Button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// --- Return All Popup ---

interface ReturnAllPopupProps {
  isOpen: boolean;
  onClose: (open: boolean) => void;
}

export const ReturnAllPopup: React.FC<ReturnAllPopupProps> = ({
  isOpen,
  onClose,
}) => {
  const { returnAllGames } = useGameManager();
  const [stats, setStats] = useState<GameReturnResponse[] | null>(null);
  const [errors, setErrors] = useState<string | null>(null);
  const [confirmed, setConfirmed] = useState(false);
  const [executing, setExecuting] = useState(false);

  useEffect(() => {
    if (!isOpen) {
      setStats(null);
      setErrors(null);
      setConfirmed(false);
      setExecuting(false);
    }
  }, [isOpen]);

  useEffect(() => {
    if (confirmed && !executing) {
      setExecuting(true);
      (async () => {
        try {
          const data = await returnAllGames();
          setStats(data);
        } catch (error) {
          setErrors(String(error));
        }
      })();
    }
  }, [confirmed, executing, returnAllGames]);

  if (!confirmed) {
    return (
      <AlertDialog
        open={isOpen}
        onOpenChange={(open) => {
          if (!open) onClose(false);
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Return All Games</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to return all games? This will reset the
              available copies of every game back to its full quantity.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => onClose(false)}>
              Cancel
            </AlertDialogCancel>
            <AlertDialogAction onClick={() => setConfirmed(true)}>
              Yes, Return All
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    );
  }

  return (
    <Dialog
      open={isOpen}
      onOpenChange={(open) => {
        if (!open) onClose(false);
      }}
    >
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Games Marked as Returned</DialogTitle>
          <DialogDescription>
            These games were not marked as returned already, do you have extra
            wiscards?
          </DialogDescription>
        </DialogHeader>
        {!stats && !errors ? (
          <div className="space-y-2">
            {Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} className="h-6" />
            ))}
          </div>
        ) : errors ? (
          <p className="text-destructive text-sm">Error: {errors}</p>
        ) : stats && stats.length === 0 ? (
          <p className="text-muted-foreground">No games were updated.</p>
        ) : (
          <div className="overflow-y-auto max-h-96 space-y-1">
            {stats?.map((game) => (
              <div
                key={game.id}
                className="flex justify-between items-center py-1.5 px-2 rounded bg-muted/50 text-sm"
              >
                <span className="font-medium">{game.name}</span>
                <span className="text-muted-foreground">
                  Qty: {game.quantity}
                </span>
              </div>
            ))}
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
};

// --- Import Popup ---

interface ImportPopupProps {
  isOpen: boolean;
  onClose: (open: boolean) => void;
}

export const ImportPopup: React.FC<ImportPopupProps> = ({
  isOpen,
  onClose,
}) => {
  const [file, setFile] = useState<File | null>(null);
  const { importFile, loading } = useGameManager();

  const handleImport = () => {
    if (!file) return;
    importFile(file);
    onClose(false);
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Import Games</DialogTitle>
          <DialogDescription>
            Import games from a CSV file. Duplicates (by name) are skipped.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4">
          <a href="importTemplate.csv" download="WUDGames-ImportTemplate">
            <Button variant="outline" size="sm" className="w-full">
              <Download className="w-4 h-4 mr-2" />
              Download Template
            </Button>
          </a>
          <div className="space-y-1.5">
            <Label className="text-xs">Upload CSV</Label>
            <input
              type="file"
              accept=".csv"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              disabled={loading}
              className="mt-1 w-full text-sm"
            />
          </div>
        </div>
        <DialogFooter>
          <Button onClick={handleImport} disabled={!file}>
            {loading ? "Importing..." : "Import"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

// --- Skeleton Card for Loading ---

const SkeletonCard = () => (
  <Card className="w-full overflow-hidden">
    <div className="aspect-[3/2] bg-muted">
      <Skeleton className="w-full h-full" />
    </div>
    <CardContent className="p-3 space-y-2">
      <Skeleton className="h-4 w-3/4" />
      <Skeleton className="h-3 w-1/2" />
      <Skeleton className="h-3 w-2/3" />
    </CardContent>
  </Card>
);

// --- Games List ---

const GamesList: React.FC<{
  onAddGame: () => void;
}> = ({ onAddGame }) => {
  const { games, loading } = useGameManager();
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4">
      {loading ? (
        <>
          {Array.from({ length: 12 }).map((_, i) => (
            <SkeletonCard key={i} />
          ))}
        </>
      ) : games.length === 0 ? (
        <div className="col-span-full text-center py-16">
          <Dice6 className="w-12 h-12 mx-auto text-muted-foreground/30 mb-4" />
          <p className="text-muted-foreground text-lg">
            No games found matching your filters.
          </p>
          {isAdmin && (
            <Button onClick={onAddGame} className="mt-4">
              <Plus className="w-4 h-4 mr-2" /> Add a Game
            </Button>
          )}
        </div>
      ) : (
        games.map((game) => <GameCard key={game.id} game={game} />)
      )}
    </div>
  );
};

// --- Main Page ---

const BoardgameMain = () => {
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";
  const isHost = isAdmin || auth?.authenticationLevel.toLowerCase() === "host";
  const [showStats, setShowStats] = useState(false);
  const [showImport, setShowImport] = useState(false);
  const [isAddGameOpen, setIsAddGameOpen] = useState(false);
  const [showReturnAll, setShowReturnAll] = useState(false);
  const { exportFile, addGame } = useGameManager();

  const handleExport = () => exportFile();
  const handleAddGame = async (game: Partial<Game>) => {
    await addGame(game);
    setIsAddGameOpen(false);
  };

  return (
    <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 py-6">
      <PageHeader
        title="Board Games"
        description="Browse and manage the board game collection."
      >
        {isAdmin && (
          <>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsAddGameOpen(true)}
            >
              <Plus className="w-4 h-4 mr-1" /> Add
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setShowImport(true)}
            >
              <Upload className="w-4 h-4 mr-1" /> Import
            </Button>
            <Button variant="outline" size="sm" onClick={handleExport}>
              <Download className="w-4 h-4 mr-1" /> Export
            </Button>
          </>
        )}
        {isHost && (
          <>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setShowReturnAll(true)}
            >
              <RefreshCw className="w-4 h-4 mr-1" /> Return All
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setShowStats(true)}
            >
              <BarChart className="w-4 h-4 mr-1" /> Stats
            </Button>
          </>
        )}
      </PageHeader>

      <div className="mb-6">
        <InlineFilters />
      </div>

      <GamesList onAddGame={() => setIsAddGameOpen(true)} />

      {/* Popups */}
      <StatsPopup isOpen={showStats} onClose={() => setShowStats(false)} />
      <ReturnAllPopup
        isOpen={showReturnAll}
        onClose={() => setShowReturnAll(false)}
      />
      <ImportPopup isOpen={showImport} onClose={() => setShowImport(false)} />
      {isAddGameOpen && (
        <AddGamePopup
          onSubmit={handleAddGame}
          isOpen={isAddGameOpen}
          onClose={() => setIsAddGameOpen(false)}
        />
      )}
    </div>
  );
};

export default BoardgameMain;
