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
import { Plus } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { SteamAccountRequest } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";
import RequestFormDialog from "@/steam/RequestFormDialog";
import ApproveDialog from "@/steam/ApproveDialog";

// --- Status Badge ---

const StatusBadge: React.FC<{ status: string }> = ({ status }) => {
  const colorMap: Record<string, string> = {
    PENDING: "bg-warning/10 text-warning",
    APPROVED: "bg-success/10 text-success",
    DENIED: "bg-destructive/10 text-destructive",
    RETURNED: "bg-primary/10 text-primary",
  };

  return (
    <span
      className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${colorMap[status] || "bg-muted text-muted-foreground"}`}
    >
      {status}
    </span>
  );
};

// --- Lending Requests Tab ---

const LendingRequestsTab: React.FC = () => {
  const { requests, loadingRequests, denyRequest, returnRequest } = useSteam();
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";
  const isHost = isAdmin || auth?.authenticationLevel.toLowerCase() === "host";

  const [isRequestFormOpen, setIsRequestFormOpen] = useState(false);
  const [approveTarget, setApproveTarget] =
    useState<SteamAccountRequest | null>(null);

  return (
    <div>
      <div className="mb-6 flex flex-wrap gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={() => setIsRequestFormOpen(true)}
          className="flex items-center gap-2"
        >
          <Plus className="w-4 h-4" /> Request a Game
        </Button>
      </div>

      {isHost ? (
        loadingRequests ? (
          <div className="space-y-2">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-10 w-full" />
            ))}
          </div>
        ) : requests.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-muted-foreground text-lg">
              No lending requests found.
            </p>
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Requester</TableHead>
                <TableHead>Game</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Dates</TableHead>
                <TableHead>Assigned Account</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {requests.map((request) => (
                <TableRow key={request.id}>
                  <TableCell>
                    <div>
                      <p className="font-medium">{request.name}</p>
                      <p className="text-xs text-muted-foreground">
                        {request.email}
                      </p>
                    </div>
                  </TableCell>
                  <TableCell>{request.gameName}</TableCell>
                  <TableCell>
                    <StatusBadge status={request.status} />
                  </TableCell>
                  <TableCell className="text-sm text-muted-foreground">
                    {request.rentalStartDay && request.rentalEndDay
                      ? `${request.rentalStartDay} - ${request.rentalEndDay}`
                      : request.rentalStartDay || request.rentalEndDay || "-"}
                  </TableCell>
                  <TableCell>
                    {request.assignedAccount
                      ? request.assignedAccount.steamAccountUsername
                      : "-"}
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      {request.status === "PENDING" && isAdmin && (
                        <>
                          <Button
                            size="sm"
                            onClick={() => setApproveTarget(request)}
                          >
                            Approve
                          </Button>
                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => void denyRequest(request.id)}
                          >
                            Deny
                          </Button>
                        </>
                      )}
                      {request.status === "APPROVED" && isHost && (
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => void returnRequest(request.id)}
                        >
                          Mark Returned
                        </Button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )
      ) : (
        <div className="text-center py-12">
          <p className="text-muted-foreground text-lg">
            Use the button above to submit a game lending request.
          </p>
        </div>
      )}

      <RequestFormDialog
        isOpen={isRequestFormOpen}
        onClose={() => setIsRequestFormOpen(false)}
      />

      {approveTarget && (
        <ApproveDialog
          isOpen={Boolean(approveTarget)}
          onClose={() => setApproveTarget(null)}
          request={approveTarget}
        />
      )}
    </div>
  );
};

export default LendingRequestsTab;
