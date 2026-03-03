import React, { useState, useEffect } from "react";
import { useAuth } from "@/AuthContext";
import {
  ConsoleGame,
  ConsoleProvider,
  useConsoleContext,
} from "@/consolegames/ConsoleGameManagerContext";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Filter,
  Gamepad2,
  Monitor,
  Pencil,
  Plus,
  Trash2,
  X,
} from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
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
import { Skeleton } from "@/components/ui/skeleton";
import { GamePopup } from "@/consolegames/ConsoleGamePopups";
import ConsolePopups from "@/consolegames/ConsolePopups";
import { Input } from "@/components/ui/input";
import PageHeader from "@/components/PageHeader";

// --- Console Game Card ---

interface ConsoleGameCardProps {
  game: ConsoleGame;
}

const ConsoleGameCard: React.FC<ConsoleGameCardProps> = ({ game }) => {
  const { auth } = useAuth();
  const [editingGame, setEditingGame] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showInfoDialog, setShowInfoDialog] = useState(false);
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";
  const { deleteGame } = useConsoleContext();

  const handleDelete = async () => {
    deleteGame(game.id);
  };

  const closeEditPopup = () => {
    setEditingGame(false);
  };

  return (
    <>
      <Card
        className={`w-full relative flex flex-col overflow-hidden hover:shadow-md hover:border-primary/30 transition-all duration-200 cursor-pointer group ${isAdmin ? "pb-10" : ""}`}
        onClick={() => setShowInfoDialog(true)}
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
              <Gamepad2 className="w-12 h-12 text-muted-foreground/30" />
            </div>
          )}
        </div>

        {/* Content Section */}
        <CardContent className="p-3 flex-1">
          <h3 className="font-semibold text-sm leading-tight line-clamp-2 mb-1">
            {game.name}
          </h3>
          <div className="space-y-1 text-xs text-muted-foreground">
            <div className="flex items-center gap-1.5">
              <Monitor className="w-3 h-3 shrink-0" />
              <span className="truncate">
                {game.consoles.map((c) => c.name).join(", ") || "N/A"}
              </span>
            </div>
          </div>
        </CardContent>

        {/* Admin Actions */}
        {isAdmin && (
          <div className="absolute bottom-0 left-0 right-0 px-2 py-1.5 border-t bg-card/80 backdrop-blur-sm flex justify-end items-center">
            <div className="flex gap-1">
              <Button
                variant="ghost"
                size="icon"
                className="h-7 w-7"
                onClick={(e) => {
                  e.stopPropagation();
                  setEditingGame(true);
                }}
              >
                <Pencil className="w-3.5 h-3.5" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                className="h-7 w-7 text-destructive hover:text-destructive"
                onClick={(e) => {
                  e.stopPropagation();
                  setShowDeleteDialog(true);
                }}
              >
                <Trash2 className="w-3.5 h-3.5" />
              </Button>
            </div>
          </div>
        )}
      </Card>

      {/* Game Info Dialog */}
      <Dialog open={showInfoDialog} onOpenChange={setShowInfoDialog}>
        <DialogContent className="max-w-2xl max-h-[90vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="text-2xl">{game.name}</DialogTitle>
            <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
              <span className="flex items-center gap-1">
                <Monitor className="w-3.5 h-3.5" />
                {game.consoles.map((c) => c.name).join(", ") || "N/A"}
              </span>
            </div>
          </DialogHeader>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 overflow-y-auto">
            <div className="md:col-span-2 space-y-4">
              <div>
                <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                  Description
                </h4>
                <p className="text-sm mt-1">
                  {game.description || "No description available."}
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                  Details
                </h4>
                <div className="grid grid-cols-2 gap-4 mt-2">
                  <div className="rounded-lg bg-muted/50 p-3">
                    <p className="text-xs text-muted-foreground uppercase tracking-wide">
                      Release Date
                    </p>
                    <p className="text-sm font-medium mt-1">
                      {game.releaseDate || "N/A"}
                    </p>
                  </div>
                  <div className="rounded-lg bg-muted/50 p-3">
                    <p className="text-xs text-muted-foreground uppercase tracking-wide">
                      Genre
                    </p>
                    <p className="text-sm font-medium mt-1">
                      {game.genres?.map((g) => g.name).join(", ") || "N/A"}
                    </p>
                  </div>
                </div>
              </div>
            </div>
            <div className="flex justify-center">
              {game.boxImageUrl && (
                <img
                  src={game.boxImageUrl}
                  alt={game.name}
                  className="w-full max-h-64 object-contain rounded-lg"
                />
              )}
            </div>
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Close</Button>
            </DialogClose>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {editingGame && (
        <GamePopup
          gameToEdit={game}
          onClose={closeEditPopup}
          isOpen={editingGame}
        />
      )}

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Game</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete "{game.name}"? This action cannot
              be undone.
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

// --- Skeleton Card ---

const SkeletonCard = () => (
  <Card className="w-full overflow-hidden">
    <div className="aspect-[3/2] bg-muted">
      <Skeleton className="w-full h-full" />
    </div>
    <CardContent className="p-3 space-y-2">
      <Skeleton className="h-4 w-3/4" />
      <Skeleton className="h-3 w-1/2" />
    </CardContent>
  </Card>
);

// --- Console Games List ---

const ConsoleGamesList = () => {
  const { filteredGames, loading } = useConsoleContext();

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4">
      {loading ? (
        <>
          {Array.from({ length: 12 }).map((_, i) => (
            <SkeletonCard key={i} />
          ))}
        </>
      ) : filteredGames.length === 0 ? (
        <div className="col-span-full text-center py-16">
          <Gamepad2 className="w-12 h-12 mx-auto text-muted-foreground/30 mb-4" />
          <p className="text-muted-foreground text-lg">
            No console games found matching your filters.
          </p>
        </div>
      ) : (
        filteredGames.map((game) => (
          <ConsoleGameCard key={game.id} game={game} />
        ))
      )}
    </div>
  );
};

// --- Inline Filters ---

const InlineFilters = () => {
  const { consoles, filters, setFilters } = useConsoleContext();

  const [isVisible, setIsVisible] = useState(false);
  const [tempFilters, setTempFilters] = useState({
    gameName: filters.gameName,
    consoleId: filters.consoleId,
  });

  useEffect(() => {
    setTempFilters({
      gameName: filters.gameName,
      consoleId: filters.consoleId,
    });
  }, [filters]);

  const hasActiveFilters =
    tempFilters.gameName !== "" || tempFilters.consoleId !== null;

  const handleClear = () => {
    const cleared = { gameName: "", consoleId: null };
    setTempFilters(cleared);
    setFilters(cleared);
  };

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
              <Label className="text-xs">Game Name</Label>
              <Input
                value={tempFilters.gameName}
                onChange={(e) => {
                  const updated = { ...tempFilters, gameName: e.target.value };
                  setTempFilters(updated);
                  setFilters(updated);
                }}
                placeholder="Search by name..."
              />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Console</Label>
              <Select
                value={tempFilters.consoleId?.toString() || "all"}
                onValueChange={(value) => {
                  const updated = {
                    ...tempFilters,
                    consoleId: value === "all" ? null : parseInt(value),
                  };
                  setTempFilters(updated);
                  setFilters(updated);
                }}
              >
                <SelectTrigger>
                  <SelectValue placeholder="All consoles" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All consoles</SelectItem>
                  {consoles.map((console) => (
                    <SelectItem key={console.id} value={console.id!.toString()}>
                      {console.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex items-end">
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
  );
};

// --- Main Page ---

const ConsolegameMain = () => {
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";

  const [isAddGameOpen, setIsAddGameOpen] = useState(false);

  return (
    <ConsoleProvider>
      <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 py-6">
        <PageHeader
          title="Console Games"
          description="Browse and manage the console game collection."
        >
          {isAdmin && (
            <>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setIsAddGameOpen(true)}
              >
                <Plus className="w-4 h-4 mr-1" /> Add Game
              </Button>
              <ConsolePopups />
            </>
          )}
        </PageHeader>

        <div className="mb-6">
          <InlineFilters />
        </div>

        <ConsoleGamesList />

        <GamePopup
          isOpen={isAddGameOpen}
          onClose={() => setIsAddGameOpen(false)}
        />
      </div>
    </ConsoleProvider>
  );
};

export default ConsolegameMain;
