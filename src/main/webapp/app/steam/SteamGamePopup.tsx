import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Loader2 } from "lucide-react";
import { SteamGame } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";

interface SteamGamePopupProps {
  isOpen: boolean;
  onClose: () => void;
  game?: SteamGame;
}

const SteamGamePopup: React.FC<SteamGamePopupProps> = ({
  isOpen,
  onClose,
  game,
}) => {
  const { addSteamGame, updateSteamGame } = useSteam();
  const [formData, setFormData] = useState<Partial<SteamGame>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (game) {
      setFormData(game);
    } else {
      setFormData({});
    }
    setError(null);
  }, [game, isOpen]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value === "" ? undefined : value });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    try {
      if (game) {
        await updateSteamGame(game.id, formData);
      } else {
        await addSteamGame(formData);
      }
      onClose();
    } catch {
      setError("An error occurred. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleDialogChange = (open: boolean) => {
    if (!open) onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleDialogChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>
            {game ? `Edit ${game.name || "Game"}` : "Add Steam Game"}
          </DialogTitle>
          <DialogDescription>
            Fill in the details for this Steam game.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="sg-name">Name</Label>
              <Input
                id="sg-name"
                type="text"
                placeholder="Game name (required)"
                name="name"
                value={formData.name || ""}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="sg-steamAppId">Steam App ID</Label>
              <Input
                id="sg-steamAppId"
                type="text"
                placeholder="Steam App ID (optional)"
                name="steamAppId"
                value={formData.steamAppId || ""}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="sg-description">Description</Label>
              <Textarea
                id="sg-description"
                placeholder="Description (optional)"
                name="description"
                value={formData.description || ""}
                onChange={handleChange}
                maxLength={1024}
                className="resize-none min-h-20"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="sg-imageUrl">Image URL</Label>
              <Input
                id="sg-imageUrl"
                type="text"
                placeholder="Image URL (optional)"
                name="imageUrl"
                value={formData.imageUrl || ""}
                onChange={handleChange}
              />
            </div>

            <div className="flex gap-6">
              <div className="flex items-center gap-2">
                <Checkbox
                  id="sg-windows"
                  checked={formData.windows ?? false}
                  onCheckedChange={(checked: boolean | "indeterminate") =>
                    setFormData({ ...formData, windows: checked === true })
                  }
                />
                <Label htmlFor="sg-windows">Windows</Label>
              </div>
              <div className="flex items-center gap-2">
                <Checkbox
                  id="sg-macos"
                  checked={formData.macos ?? false}
                  onCheckedChange={(checked: boolean | "indeterminate") =>
                    setFormData({ ...formData, macos: checked === true })
                  }
                />
                <Label htmlFor="sg-macos">macOS</Label>
              </div>
              <div className="flex items-center gap-2">
                <Checkbox
                  id="sg-linux"
                  checked={formData.linux ?? false}
                  onCheckedChange={(checked: boolean | "indeterminate") =>
                    setFormData({ ...formData, linux: checked === true })
                  }
                />
                <Label htmlFor="sg-linux">Linux</Label>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="sg-internalNotes">Internal Notes</Label>
              <Input
                id="sg-internalNotes"
                type="text"
                placeholder="Internal notes (optional)"
                name="internalNotes"
                value={formData.internalNotes || ""}
                onChange={handleChange}
              />
            </div>
          </div>

          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <DialogFooter className="flex flex-col sm:flex-row gap-2 sm:justify-between">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              className="flex-1"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={isLoading}
              className="w-full sm:w-auto"
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  {game ? "Saving..." : "Adding..."}
                </>
              ) : game ? (
                "Save"
              ) : (
                "Add"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default SteamGamePopup;
