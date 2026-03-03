import React, { useState, useEffect } from "react";
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
  RefreshCw,
  Filter,
  X,
  Package,
  MapPin,
  Tag,
} from "lucide-react";
import { useAuth } from "@/AuthContext";
import { EquipmentItem } from "@/types";
import {
  EquipmentProvider,
  useEquipment,
} from "@/equipment/EquipmentManagerContext";
import {
  AddEquipmentPopup,
  EditEquipmentPopup,
} from "@/equipment/EquipmentPopups";
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

interface EquipmentReturnResponse {
  id: number;
  name: string;
  quantity: number;
}

// --- EquipmentCard ---

interface EquipmentCardProps {
  item: EquipmentItem;
}

const EquipmentCard: React.FC<EquipmentCardProps> = ({ item }) => {
  const { auth } = useAuth();
  const { deleteEquipment, checkout, returnItem, updateEquipment } =
    useEquipment();
  const [editingItem, setEditingItem] = useState<EquipmentItem | null>(null);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showInfoDialog, setShowInfoDialog] = useState(false);
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";
  const isHost = isAdmin || auth?.authenticationLevel.toLowerCase() === "host";

  const handleEdit = async (updated: Partial<EquipmentItem>) => {
    if (!editingItem) return;
    await updateEquipment(item.id, updated);
    setEditingItem(null);
  };

  const handleDelete = async () => {
    deleteEquipment(item.id);
  };

  const handleCheckout = async () => {
    checkout(item.id);
  };

  const handleReturn = async () => {
    returnItem(item.id);
  };

  const isReturnDisabled = item.availableCopies === item.quantity;
  const isCheckoutDisabled =
    item.availableCopies === null ||
    item.availableCopies === undefined ||
    item.availableCopies <= 0;

  const availabilityColor =
    item.availableCopies === 0
      ? "text-destructive"
      : (item.availableCopies ?? 0) < (item.quantity ?? 0)
        ? "text-warning"
        : "text-success";

  return (
    <>
      <Card
        className={`w-full relative flex flex-col overflow-hidden hover:shadow-md hover:border-primary/30 transition-all duration-200 cursor-pointer group ${isHost ? "pb-12" : ""}`}
        onClick={() => setShowInfoDialog(true)}
      >
        {/* Image Section */}
        <div className="relative w-full aspect-[3/2] bg-muted overflow-hidden">
          {item.imageUrl ? (
            <img
              src={item.imageUrl}
              alt={item.name}
              className="w-full h-full object-contain p-2 group-hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <Package className="w-12 h-12 text-muted-foreground/30" />
            </div>
          )}
          {/* Availability badge */}
          <div
            className={`absolute top-2 right-2 text-xs font-semibold px-2 py-0.5 rounded-full ${
              item.availableCopies === 0
                ? "bg-destructive/10 text-destructive"
                : "bg-success/10 text-success"
            }`}
          >
            {item.availableCopies}/{item.quantity}
          </div>
        </div>

        {/* Content Section */}
        <CardContent className="p-3 flex-1">
          <h3 className="font-semibold text-sm leading-tight line-clamp-2 mb-2">
            {item.name}
          </h3>
          <div className="space-y-1 text-xs text-muted-foreground">
            {item.type && (
              <div className="flex items-center gap-1.5">
                <Tag className="w-3 h-3 shrink-0" />
                <span className="truncate">{item.type.replace(/_/g, " ")}</span>
              </div>
            )}
            {item.location && (
              <div className="flex items-center gap-1.5">
                <MapPin className="w-3 h-3 shrink-0" />
                <span className="truncate">{item.location}</span>
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
                    setEditingItem(item);
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
            <div className={`flex gap-1 ${!isAdmin ? "ml-auto" : ""}`}>
              <Button
                title="Checkout (remove a copy)"
                variant="outline"
                size="sm"
                className="h-8 px-2 text-destructive border-destructive/30 hover:bg-destructive/10"
                onClick={(e) => {
                  e.stopPropagation();
                  handleCheckout();
                }}
                disabled={isCheckoutDisabled}
              >
                <Minus className="w-3.5 h-3.5 mr-1" />
                Out
              </Button>
              <Button
                title="Return (add a copy back)"
                variant="outline"
                size="sm"
                className="h-8 px-2 text-success border-success/30 hover:bg-success/10"
                onClick={(e) => {
                  e.stopPropagation();
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

      {/* Info Dialog */}
      <Dialog open={showInfoDialog} onOpenChange={setShowInfoDialog}>
        <DialogContent className="max-w-2xl max-h-[90vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="text-2xl">{item.name}</DialogTitle>
            <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
              {item.type && (
                <span className="flex items-center gap-1">
                  <Tag className="w-3.5 h-3.5" />
                  {item.type.replace(/_/g, " ")}
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
                  {item.description || "No description."}
                </p>
              </div>
              {isHost && (
                <>
                  {item.internalNotes && (
                    <div>
                      <h4 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                        Internal Notes
                      </h4>
                      <p className="mt-1 text-sm italic">
                        {item.internalNotes}
                      </p>
                    </div>
                  )}
                  <div className="grid grid-cols-2 gap-4">
                    <div className="rounded-lg bg-muted/50 p-3">
                      <p className="text-xs text-muted-foreground uppercase tracking-wide">
                        Availability
                      </p>
                      <p className={`text-lg font-bold ${availabilityColor}`}>
                        {item.availableCopies} / {item.quantity}
                      </p>
                    </div>
                    <div className="rounded-lg bg-muted/50 p-3">
                      <p className="text-xs text-muted-foreground uppercase tracking-wide">
                        Times Checked Out
                      </p>
                      <p className="text-lg font-bold">{item.checkoutCount}</p>
                    </div>
                  </div>
                </>
              )}
              {item.location && (
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <MapPin className="w-4 h-4" />
                  {item.location}
                </div>
              )}
            </div>
            <div className="flex justify-center">
              {item.imageUrl && (
                <img
                  src={item.imageUrl}
                  alt={item.name}
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

      <EditEquipmentPopup
        item={editingItem!}
        onSubmit={handleEdit}
        onClose={() => setEditingItem(null)}
        isOpen={Boolean(editingItem)}
      />

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Equipment</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete &quot;{item.name}&quot;? This
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

// --- Skeleton Card ---

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

// --- EquipmentList ---

const EquipmentList: React.FC<{ onAdd: () => void }> = ({ onAdd }) => {
  const { equipment, loading } = useEquipment();
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
      ) : equipment.length === 0 ? (
        <div className="col-span-full text-center py-16">
          <Package className="w-12 h-12 mx-auto text-muted-foreground/30 mb-4" />
          <p className="text-muted-foreground text-lg">
            No equipment found matching your filters.
          </p>
          {isAdmin && (
            <Button onClick={onAdd} className="mt-4">
              <Plus className="w-4 h-4 mr-2" /> Add Equipment
            </Button>
          )}
        </div>
      ) : (
        equipment.map((item) => <EquipmentCard key={item.id} item={item} />)
      )}
    </div>
  );
};

// --- InlineFilters ---

interface InlineFilterState {
  name: string;
  type: string;
}

const InlineFilters = () => {
  const [isVisible, setIsVisible] = useState(false);
  const [filters, setFilters] = useState<InlineFilterState>({
    name: "",
    type: "",
  });
  const [sortField, setSortField] = useState("name");
  const [sortDirection, setSortDirection] = useState("asc");

  const { updateFiltersAndSort, types } = useEquipment();

  useEffect(() => {
    updateFiltersAndSort(filters, {
      field: sortField as keyof EquipmentItem,
      direction: sortDirection as "asc" | "desc",
    });
  }, [filters, sortField, sortDirection]);

  const handleClear = () => {
    setFilters({ name: "", type: "" });
    setSortField("name");
    setSortDirection("asc");
  };

  const hasActiveFilters =
    filters.name ||
    filters.type ||
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
              <Label className="text-xs">Type</Label>
              <Input
                type="text"
                list="type-suggestions"
                value={filters.type}
                onChange={(e) =>
                  setFilters({ ...filters, type: e.target.value })
                }
                placeholder="Filter by type..."
              />
              <datalist id="type-suggestions">
                {types.map((t) => (
                  <option key={t} value={t} />
                ))}
              </datalist>
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Sort By</Label>
              <Select value={sortField} onValueChange={setSortField}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Sort by" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="name">Name</SelectItem>
                  <SelectItem value="type">Type</SelectItem>
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

// --- ReturnAllEquipmentPopup ---

interface ReturnAllPopupProps {
  isOpen: boolean;
  onClose: (open: boolean) => void;
}

const ReturnAllEquipmentPopup: React.FC<ReturnAllPopupProps> = ({
  isOpen,
  onClose,
}) => {
  const { returnAllEquipment } = useEquipment();
  const [results, setResults] = useState<EquipmentReturnResponse[] | null>(
    null,
  );
  const [errors, setErrors] = useState<string | null>(null);
  const [confirmed, setConfirmed] = useState(false);
  const [executing, setExecuting] = useState(false);

  useEffect(() => {
    if (!isOpen) {
      setResults(null);
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
          const data = await returnAllEquipment();
          setResults(data);
        } catch (error) {
          setErrors(String(error));
        }
      })();
    }
  }, [confirmed, executing, returnAllEquipment]);

  const handleConfirm = () => {
    setConfirmed(true);
  };

  const handleClose = () => {
    onClose(false);
  };

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
            <AlertDialogTitle>Return All Equipment</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to return all equipment? This will reset the
              available quantity of every item back to its full quantity.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={handleClose}>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirm}>
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
          <DialogTitle>Equipment Marked as Returned</DialogTitle>
          <DialogDescription>
            The following equipment was not marked as returned already.
          </DialogDescription>
        </DialogHeader>
        {!results && !errors ? (
          <div className="space-y-2">
            {Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} className="h-6" />
            ))}
          </div>
        ) : errors ? (
          <p className="text-destructive text-sm">Error: {errors}</p>
        ) : results && results.length === 0 ? (
          <p className="text-muted-foreground">No equipment was updated.</p>
        ) : (
          <div className="overflow-y-auto max-h-96 space-y-1">
            {results?.map((item) => (
              <div
                key={item.id}
                className="flex justify-between items-center py-1.5 px-2 rounded bg-muted/50 text-sm"
              >
                <span className="font-medium">{item.name}</span>
                <span className="text-muted-foreground">
                  Qty: {item.quantity}
                </span>
              </div>
            ))}
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
};

// --- EquipmentMain ---

const EquipmentMainContent = () => {
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";
  const isHost = isAdmin || auth?.authenticationLevel.toLowerCase() === "host";
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [showReturnAll, setShowReturnAll] = useState(false);
  const { addEquipment } = useEquipment();

  const handleAdd = async (data: Partial<EquipmentItem>) => {
    await addEquipment(data);
    setIsAddOpen(false);
  };

  return (
    <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 py-6">
      <PageHeader
        title="Equipment"
        description="Browse and manage equipment inventory."
      >
        {isAdmin && (
          <Button
            variant="outline"
            size="sm"
            onClick={() => setIsAddOpen(true)}
          >
            <Plus className="w-4 h-4 mr-1" /> Add Equipment
          </Button>
        )}
        {isHost && (
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowReturnAll(true)}
          >
            <RefreshCw className="w-4 h-4 mr-1" /> Return All
          </Button>
        )}
      </PageHeader>

      <div className="mb-6">
        <InlineFilters />
      </div>

      <EquipmentList onAdd={() => setIsAddOpen(true)} />

      <ReturnAllEquipmentPopup
        isOpen={showReturnAll}
        onClose={() => setShowReturnAll(false)}
      />
      {isAddOpen && (
        <AddEquipmentPopup
          onSubmit={handleAdd}
          isOpen={isAddOpen}
          onClose={() => setIsAddOpen(false)}
        />
      )}
    </div>
  );
};

const EquipmentMain = () => {
  return (
    <EquipmentProvider>
      <EquipmentMainContent />
    </EquipmentProvider>
  );
};

export default EquipmentMain;
