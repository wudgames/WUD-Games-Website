import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
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
import { Pencil, Trash2, Plus } from "lucide-react";
import { SteamAccount } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";
import SteamAccountPopup from "@/steam/SteamAccountPopup";

const SteamAccountsTab: React.FC = () => {
  const { steamAccounts, requests, loadingAccounts, deleteSteamAccount } =
    useSteam();
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [editingAccount, setEditingAccount] = useState<SteamAccount | null>(
    null,
  );
  const [deleteTarget, setDeleteTarget] = useState<SteamAccount | null>(null);

  const getActiveRequestCount = (accountId: number): number => {
    return requests.filter(
      (r) => r.assignedAccount?.id === accountId && r.status === "APPROVED",
    ).length;
  };

  return (
    <div>
      <div className="mb-6">
        <Button
          variant="outline"
          size="sm"
          onClick={() => setIsAddOpen(true)}
          className="flex items-center gap-2"
        >
          <Plus className="w-4 h-4" /> Add Account
        </Button>
      </div>

      {loadingAccounts ? (
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-10 w-full" />
          ))}
        </div>
      ) : steamAccounts.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-muted-foreground text-lg">
            No Steam accounts configured.
          </p>
          <Button onClick={() => setIsAddOpen(true)} className="mt-4">
            <Plus className="w-4 h-4 mr-2" /> Add Account
          </Button>
        </div>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Username</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Active Requests</TableHead>
              <TableHead>Notes</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {steamAccounts.map((account) => (
              <TableRow key={account.id}>
                <TableCell className="font-medium">
                  {account.steamAccountUsername}
                </TableCell>
                <TableCell>
                  <span
                    className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                      account.available
                        ? "bg-success/10 text-success"
                        : "bg-destructive/10 text-destructive"
                    }`}
                  >
                    {account.available ? "Available" : "Unavailable"}
                  </span>
                </TableCell>
                <TableCell>{getActiveRequestCount(account.id)}</TableCell>
                <TableCell className="text-muted-foreground">
                  {account.notes || "-"}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8"
                      onClick={() => setEditingAccount(account)}
                    >
                      <Pencil className="w-3.5 h-3.5" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8 text-destructive hover:text-destructive"
                      onClick={() => setDeleteTarget(account)}
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      <SteamAccountPopup
        isOpen={isAddOpen}
        onClose={() => setIsAddOpen(false)}
      />

      {editingAccount && (
        <SteamAccountPopup
          isOpen={Boolean(editingAccount)}
          onClose={() => setEditingAccount(null)}
          account={editingAccount}
        />
      )}

      <AlertDialog
        open={Boolean(deleteTarget)}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Account</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete account &quot;
              {deleteTarget?.steamAccountUsername}&quot;? This action cannot be
              undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                if (deleteTarget) {
                  void deleteSteamAccount(deleteTarget.id);
                  setDeleteTarget(null);
                }
              }}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export default SteamAccountsTab;
