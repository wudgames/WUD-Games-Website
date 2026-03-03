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
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { CheckCircle2, Copy, Loader2, Mail } from "lucide-react";
import { SteamAccountRequest } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";

interface ApproveDialogProps {
  isOpen: boolean;
  onClose: () => void;
  request: SteamAccountRequest;
}

const ApproveDialog: React.FC<ApproveDialogProps> = ({
  isOpen,
  onClose,
  request,
}) => {
  const { steamAccounts, approveRequest } = useSteam();
  const [selectedAccountId, setSelectedAccountId] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const [approved, setApproved] = useState(false);
  const [assignedUsername, setAssignedUsername] = useState("");
  const [copied, setCopied] = useState(false);

  const availableAccounts = steamAccounts.filter((a) => a.available);

  useEffect(() => {
    if (isOpen) {
      setSelectedAccountId("");
      setApproved(false);
      setAssignedUsername("");
      setCopied(false);
    }
  }, [isOpen]);

  const handleApprove = async () => {
    if (!selectedAccountId) return;
    setIsLoading(true);
    try {
      await approveRequest(request.id, parseInt(selectedAccountId));
      const account = steamAccounts.find(
        (a) => a.id === parseInt(selectedAccountId),
      );
      setAssignedUsername(account?.steamAccountUsername ?? selectedAccountId);
      setApproved(true);
    } catch {
      console.error("Failed to approve request");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCopyEmail = async () => {
    try {
      await navigator.clipboard.writeText(request.email);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Clipboard API may not be available
    }
  };

  if (approved) {
    return (
      <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2 text-success">
              <CheckCircle2 className="h-5 w-5" />
              Request Approved
            </DialogTitle>
            <DialogDescription>
              The account has been assigned. Please send the credentials to the
              requester.
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">
            <div className="rounded-lg border border-warning/30 bg-warning/5 p-4 space-y-3">
              <div className="flex items-center gap-2 text-sm font-medium text-warning">
                <Mail className="h-4 w-4" />
                Don&apos;t forget to email the account credentials!
              </div>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Requester:</span>
                  <span className="font-medium">{request.name}</span>
                </div>
                <div className="flex items-center justify-between gap-2">
                  <span className="text-muted-foreground">Email:</span>
                  <span className="flex items-center gap-1.5">
                    <span className="font-medium">{request.email}</span>
                    <button
                      type="button"
                      onClick={() => void handleCopyEmail()}
                      className="text-muted-foreground hover:text-foreground transition-colors"
                      title="Copy email"
                    >
                      <Copy className="h-3.5 w-3.5" />
                    </button>
                    {copied && (
                      <span className="text-xs text-success">Copied!</span>
                    )}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Game:</span>
                  <span className="font-medium">{request.gameName}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Account:</span>
                  <span className="font-medium">{assignedUsername}</span>
                </div>
              </div>
            </div>
          </div>

          <DialogFooter>
            <Button onClick={onClose}>Done</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Approve Request</DialogTitle>
          <DialogDescription>
            Select an available Steam account to assign to {request.name} for
            &quot;{request.gameName}&quot;.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div className="space-y-2">
            <Label>Assign Account</Label>
            {availableAccounts.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                No available accounts. Please make an account available first.
              </p>
            ) : (
              <Select
                value={selectedAccountId}
                onValueChange={setSelectedAccountId}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select an account..." />
                </SelectTrigger>
                <SelectContent>
                  {availableAccounts.map((account) => (
                    <SelectItem key={account.id} value={String(account.id)}>
                      {account.steamAccountUsername}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button
            onClick={() => void handleApprove()}
            disabled={!selectedAccountId || isLoading}
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Approving...
              </>
            ) : (
              "Approve"
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ApproveDialog;
