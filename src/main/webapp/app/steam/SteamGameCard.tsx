import React, { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from "@/components/ui/dialog";
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
import { Pencil, Trash2, Monitor } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { SteamGame } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";
import SteamGamePopup from "@/steam/SteamGamePopup";

interface SteamGameCardProps {
  game: SteamGame;
}

const SteamGameCard: React.FC<SteamGameCardProps> = ({ game }) => {
  const { auth } = useAuth();
  const { deleteSteamGame } = useSteam();
  const [editingGame, setEditingGame] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showInfoDialog, setShowInfoDialog] = useState(false);
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";

  const handleDelete = () => {
    void deleteSteamGame(game.id);
  };

  const platforms: string[] = [];
  if (game.windows) platforms.push("Windows");
  if (game.macos) platforms.push("macOS");
  if (game.linux) platforms.push("Linux");

  return (
    <>
      <Card
        className={`w-full relative flex flex-col overflow-hidden hover:shadow-md hover:border-primary/30 transition-all duration-200 cursor-pointer group ${isAdmin ? "pb-10" : ""}`}
        onClick={() => setShowInfoDialog(true)}
      >
        {/* Image Section */}
        <div className="relative w-full aspect-[3/2] bg-muted overflow-hidden">
          {game.imageUrl ? (
            <img
              src={game.imageUrl}
              alt={game.name}
              className="w-full h-full object-contain p-2 group-hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <Monitor className="w-12 h-12 text-muted-foreground/30" />
            </div>
          )}
        </div>

        {/* Content Section */}
        <CardContent className="p-3 flex-1">
          <h3 className="font-semibold text-sm leading-tight line-clamp-2 mb-1 text-center">
            {game.name}
          </h3>
          {platforms.length > 0 && (
            <div className="flex gap-1 justify-center mt-1 flex-wrap">
              {platforms.map((p) => (
                <span
                  key={p}
                  className="text-xs px-1.5 py-0.5 rounded bg-muted text-muted-foreground"
                >
                  {p}
                </span>
              ))}
            </div>
          )}
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
            {platforms.length > 0 && (
              <div className="flex gap-1 flex-wrap">
                {platforms.map((p) => (
                  <span
                    key={p}
                    className="text-xs px-2 py-0.5 rounded bg-muted text-muted-foreground"
                  >
                    {p}
                  </span>
                ))}
              </div>
            )}
          </DialogHeader>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 overflow-y-auto">
            <div className="md:col-span-2 space-y-4">
              <div>
                <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                  Description
                </h4>
                <p className="mt-1 text-sm">
                  {game.description || "No description."}
                </p>
              </div>
              {game.steamAppId && (
                <div className="rounded-lg bg-muted/50 p-3">
                  <p className="text-xs text-muted-foreground uppercase tracking-wide">
                    Steam App ID
                  </p>
                  <p className="text-sm font-medium mt-1">{game.steamAppId}</p>
                </div>
              )}
              {isAdmin && game.internalNotes && (
                <div>
                  <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                    Internal Notes
                  </h4>
                  <p className="mt-1 text-sm italic">{game.internalNotes}</p>
                </div>
              )}
            </div>
            <div className="flex justify-center">
              {game.imageUrl && (
                <img
                  src={game.imageUrl}
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
        <SteamGamePopup
          game={game}
          onClose={() => setEditingGame(false)}
          isOpen={editingGame}
        />
      )}

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

export default SteamGameCard;
