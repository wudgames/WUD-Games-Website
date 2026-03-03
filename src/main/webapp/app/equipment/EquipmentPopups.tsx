import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Loader2 } from "lucide-react";
import { EquipmentItem } from "@/types";

const EQUIPMENT_TYPES = ["CONTROLLER", "JOYCON", "RPG_EQUIPMENT", "OTHER"];

interface EquipmentPopupProps {
  isOpen: boolean;
  onClose: () => void;
  item?: EquipmentItem;
  onSubmit: (data: Partial<EquipmentItem>) => Promise<void>;
}

const EquipmentPopup: React.FC<EquipmentPopupProps> = ({
  isOpen,
  onClose,
  item,
  onSubmit,
}) => {
  const [formData, setFormData] = useState<Partial<EquipmentItem>>(item || {});
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (item) {
      setFormData(item);
    } else {
      setFormData({});
    }
  }, [item, isOpen]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]:
        value === ""
          ? undefined
          : name === "quantity"
            ? parseInt(value)
            : value,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    try {
      await onSubmit(formData as Partial<EquipmentItem>);
      onClose();
    } catch (_err) {
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
            {item ? `Edit ${item.name || "Equipment"}` : "Add Equipment"}
          </DialogTitle>
          <DialogDescription>
            Fill in the details for this equipment item.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                type="text"
                placeholder="Equipment name (required)"
                name="name"
                value={formData.name || ""}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="type">Type</Label>
              <Select
                value={formData.type || ""}
                onValueChange={(value) =>
                  setFormData({ ...formData, type: value })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select type..." />
                </SelectTrigger>
                <SelectContent>
                  {EQUIPMENT_TYPES.map((t) => (
                    <SelectItem key={t} value={t}>
                      {t.replace(/_/g, " ")}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                placeholder="Description (optional)"
                name="description"
                value={formData.description || ""}
                onChange={handleChange}
                maxLength={1024}
                className="resize-none min-h-20"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="quantity">Quantity</Label>
              <Input
                id="quantity"
                type="number"
                placeholder="Quantity"
                name="quantity"
                value={
                  formData.quantity != undefined
                    ? formData.quantity.toString()
                    : ""
                }
                onChange={handleChange}
                min="0"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="imageUrl">Image URL</Label>
              <Input
                id="imageUrl"
                type="text"
                placeholder="Image URL (optional)"
                name="imageUrl"
                value={formData.imageUrl || ""}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="location">Location</Label>
              <Input
                id="location"
                type="text"
                placeholder="Location (optional)"
                name="location"
                value={formData.location || ""}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="internalNotes">Internal Notes</Label>
              <Input
                id="internalNotes"
                type="text"
                placeholder="Internal Notes (optional)"
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
                  {item ? "Saving..." : "Adding..."}
                </>
              ) : item ? (
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

export const AddEquipmentPopup: React.FC<Omit<EquipmentPopupProps, "item">> = ({
  isOpen,
  onClose,
  onSubmit,
}) => <EquipmentPopup isOpen={isOpen} onClose={onClose} onSubmit={onSubmit} />;

export const EditEquipmentPopup: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  item: EquipmentItem;
  onSubmit: (data: Partial<EquipmentItem>) => Promise<void>;
}> = ({ isOpen, onClose, item, onSubmit }) => (
  <EquipmentPopup
    isOpen={isOpen}
    onClose={onClose}
    item={item}
    onSubmit={onSubmit}
  />
);
