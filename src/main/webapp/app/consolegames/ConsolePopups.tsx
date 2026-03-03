import React, { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { MoreHorizontal, Plus, Pencil, Trash, Settings } from "lucide-react";
import {
  useConsoleContext,
  Console,
} from "@/consolegames/ConsoleGameManagerContext";

const ConsolePopups = () => {
  const { consoles, createConsole, updateConsole, deleteConsole } =
    useConsoleContext();
  const [isOpen, setIsOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [newConsoleName, setNewConsoleName] = useState("");
  const [currentConsole, setCurrentConsole] = useState<Console | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const [isManageOpen, setIsManageOpen] = useState(false);

  const handleCreateConsole = async () => {
    if (!newConsoleName.trim()) return;

    setIsLoading(true);
    try {
      await createConsole({ name: newConsoleName });
      setNewConsoleName("");
      setIsOpen(false);
    } catch (error) {
      console.error("Failed to create console:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEditConsole = async () => {
    if (!newConsoleName.trim() || !currentConsole || currentConsole.id == null)
      return;

    setIsLoading(true);
    try {
      await updateConsole(currentConsole.id, {
        ...currentConsole,
        name: newConsoleName,
      });
      setNewConsoleName("");
      setCurrentConsole(null);
      setIsEditOpen(false);
    } catch (error) {
      console.error("Failed to update console:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeleteConsole = async (id: number) => {
    setIsLoading(true);
    try {
      await deleteConsole(id);
    } catch (error) {
      console.error("Failed to delete console:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const openEditDialog = (consoleItem: Console) => {
    setCurrentConsole(consoleItem);
    setNewConsoleName(consoleItem.name);
    setIsEditOpen(true);
  };

  return (
    <>
      <Button variant="outline" size="sm" onClick={() => setIsManageOpen(true)}>
        <Settings className="w-4 h-4 mr-1" /> Consoles
      </Button>

      {/* Manage Consoles Dialog */}
      <Dialog open={isManageOpen} onOpenChange={setIsManageOpen}>
        <DialogContent className="sm:max-w-lg max-h-[80vh] flex flex-col">
          <DialogHeader>
            <DialogTitle>Manage Consoles</DialogTitle>
          </DialogHeader>
          <div className="flex mb-4">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsOpen(true)}
              className="flex items-center gap-2"
            >
              <Plus className="w-4 h-4" /> Add Console
            </Button>
          </div>
          <div className="overflow-y-auto flex-1">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead className="w-24">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {consoles.length === 0 ? (
                  <TableRow>
                    <TableCell
                      colSpan={3}
                      className="text-center py-6 text-muted-foreground"
                    >
                      No consoles found. Add your first one!
                    </TableCell>
                  </TableRow>
                ) : (
                  consoles.map((console) => (
                    <TableRow key={console.id}>
                      <TableCell>{console.id}</TableCell>
                      <TableCell>{console.name}</TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" className="h-8 w-8 p-0">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem
                              onClick={() => openEditDialog(console)}
                            >
                              <Pencil className="mr-2 h-4 w-4" />
                              Edit
                            </DropdownMenuItem>
                            <DropdownMenuItem
                              className="text-destructive"
                              onClick={() =>
                                console.id != null &&
                                handleDeleteConsole(console.id)
                              }
                            >
                              <Trash className="mr-2 h-4 w-4" />
                              Delete
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>
        </DialogContent>
      </Dialog>

      {/* Create Console Dialog */}
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Add New Console</DialogTitle>
          </DialogHeader>
          <div className="flex items-center space-x-2 py-4">
            <Input
              placeholder="Console name"
              value={newConsoleName}
              onChange={(e) => setNewConsoleName(e.target.value)}
              className="flex-1"
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsOpen(false)}>
              Cancel
            </Button>
            <Button
              onClick={handleCreateConsole}
              disabled={isLoading || !newConsoleName.trim()}
            >
              {isLoading ? "Creating..." : "Create"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Edit Console Dialog */}
      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Edit Console</DialogTitle>
          </DialogHeader>
          <div className="flex items-center space-x-2 py-4">
            <Input
              placeholder="Console name"
              value={newConsoleName}
              onChange={(e) => setNewConsoleName(e.target.value)}
              className="flex-1"
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditOpen(false)}>
              Cancel
            </Button>
            <Button
              onClick={handleEditConsole}
              disabled={isLoading || !newConsoleName.trim()}
            >
              {isLoading ? "Saving..." : "Save Changes"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default ConsolePopups;
