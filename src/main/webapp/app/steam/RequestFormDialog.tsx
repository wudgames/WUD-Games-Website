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
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Loader2 } from "lucide-react";
import { useSteam } from "@/steam/SteamManagerContext";

interface RequestFormDialogProps {
  isOpen: boolean;
  onClose: () => void;
}

const RequestFormDialog: React.FC<RequestFormDialogProps> = ({
  isOpen,
  onClose,
}) => {
  const { submitRequest } = useSteam();
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    gameName: "",
    comments: "",
    rentalStartDay: "",
    rentalEndDay: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setFormData({
        name: "",
        email: "",
        gameName: "",
        comments: "",
        rentalStartDay: "",
        rentalEndDay: "",
      });
      setError(null);
      setSuccess(false);
    }
  }, [isOpen]);

  const validate = (): string | null => {
    const trimmed = {
      name: formData.name.trim(),
      email: formData.email.trim(),
      gameName: formData.gameName.trim(),
      rentalStartDay: formData.rentalStartDay,
      rentalEndDay: formData.rentalEndDay,
    };

    if (!trimmed.name) return "Name is required.";
    if (!trimmed.email) return "Email is required.";
    if (!trimmed.gameName) return "Game name is required.";
    if (!trimmed.rentalStartDay) return "Start date is required.";
    if (!trimmed.rentalEndDay) return "End date is required.";

    if (trimmed.rentalStartDay >= trimmed.rentalEndDay) {
      return "Start date must be before end date.";
    }

    return null;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setIsLoading(true);
    try {
      await submitRequest({
        name: formData.name.trim(),
        email: formData.email.trim(),
        gameName: formData.gameName.trim(),
        comments: formData.comments.trim() || undefined,
        rentalStartDay: formData.rentalStartDay,
        rentalEndDay: formData.rentalEndDay,
      });
      setSuccess(true);
    } catch {
      setError("Failed to submit request. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  if (success) {
    return (
      <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Request Submitted</DialogTitle>
            <DialogDescription>
              Your game lending request has been submitted successfully. You
              will be contacted when your request is reviewed.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button onClick={onClose}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Request a Game</DialogTitle>
          <DialogDescription>
            Fill in the form to request access to a Steam game.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="req-name">Your Name</Label>
              <Input
                id="req-name"
                type="text"
                placeholder="Your name (required)"
                value={formData.name}
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="req-email">Email</Label>
              <Input
                id="req-email"
                type="email"
                placeholder="Your email (required)"
                value={formData.email}
                onChange={(e) =>
                  setFormData({ ...formData, email: e.target.value })
                }
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="req-gameName">Game Name</Label>
              <Input
                id="req-gameName"
                type="text"
                placeholder="Name of the game (required)"
                value={formData.gameName}
                onChange={(e) =>
                  setFormData({ ...formData, gameName: e.target.value })
                }
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="req-startDay">Start Date</Label>
                <Input
                  id="req-startDay"
                  type="date"
                  value={formData.rentalStartDay}
                  onChange={(e) =>
                    setFormData({ ...formData, rentalStartDay: e.target.value })
                  }
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="req-endDay">End Date</Label>
                <Input
                  id="req-endDay"
                  type="date"
                  value={formData.rentalEndDay}
                  onChange={(e) =>
                    setFormData({ ...formData, rentalEndDay: e.target.value })
                  }
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="req-comments">Comments</Label>
              <Textarea
                id="req-comments"
                placeholder="Any additional comments (optional)"
                value={formData.comments}
                onChange={(e) =>
                  setFormData({ ...formData, comments: e.target.value })
                }
                className="resize-none min-h-20"
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
                  Submitting...
                </>
              ) : (
                "Submit Request"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default RequestFormDialog;
