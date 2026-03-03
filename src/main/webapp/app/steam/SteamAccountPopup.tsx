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
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Loader2 } from "lucide-react";
import { SteamAccount } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";

interface SteamAccountPopupProps {
  isOpen: boolean;
  onClose: () => void;
  account?: SteamAccount;
}

const SteamAccountPopup: React.FC<SteamAccountPopupProps> = ({
  isOpen,
  onClose,
  account,
}) => {
  const { addSteamAccount, updateSteamAccount } = useSteam();
  const [formData, setFormData] = useState<Partial<SteamAccount>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (account) {
      setFormData(account);
    } else {
      setFormData({ available: true });
    }
    setError(null);
  }, [account, isOpen]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    try {
      if (account) {
        await updateSteamAccount(account.id, formData);
      } else {
        await addSteamAccount(formData);
      }
      onClose();
    } catch {
      setError("An error occurred. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>
            {account ? "Edit Account" : "Add Steam Account"}
          </DialogTitle>
          <DialogDescription>
            Enter the account details. No passwords are stored.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="sa-username">Username</Label>
              <Input
                id="sa-username"
                type="text"
                placeholder="Steam username (required)"
                value={formData.steamAccountUsername || ""}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    steamAccountUsername: e.target.value,
                  })
                }
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="sa-notes">Notes</Label>
              <Input
                id="sa-notes"
                type="text"
                placeholder="Notes (optional)"
                value={formData.notes || ""}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    notes: e.target.value || undefined,
                  })
                }
              />
            </div>

            <div className="flex items-center gap-2">
              <Checkbox
                id="sa-available"
                checked={formData.available ?? true}
                onCheckedChange={(checked: boolean | "indeterminate") =>
                  setFormData({ ...formData, available: checked === true })
                }
              />
              <Label htmlFor="sa-available">Available</Label>
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
                  {account ? "Saving..." : "Adding..."}
                </>
              ) : account ? (
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

export default SteamAccountPopup;
