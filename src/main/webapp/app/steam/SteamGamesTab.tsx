import React, { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Plus, Search, Monitor } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { SteamGame } from "@/types";
import { useSteam } from "@/steam/SteamManagerContext";
import SteamGamePopup from "@/steam/SteamGamePopup";
import SteamSearchDialog from "@/steam/SteamSearchDialog";
import SteamGameCard from "@/steam/SteamGameCard";

// --- Skeleton Card ---

const SkeletonCard = () => (
  <Card className="w-full overflow-hidden">
    <div className="aspect-[3/2] bg-muted">
      <Skeleton className="w-full h-full" />
    </div>
    <CardContent className="p-3 space-y-2">
      <Skeleton className="h-4 w-3/4 mx-auto" />
      <Skeleton className="h-3 w-1/2 mx-auto" />
    </CardContent>
  </Card>
);

const SteamGamesTab: React.FC = () => {
  const { steamGames, loadingGames } = useSteam();
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";

  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [addFromSearch, setAddFromSearch] = useState<Partial<SteamGame> | null>(
    null,
  );

  const handleSearchSelect = (gameData: Partial<SteamGame>) => {
    setAddFromSearch(gameData);
    setIsAddOpen(true);
  };

  return (
    <div>
      {isAdmin && (
        <div className="mb-6 flex flex-wrap gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setAddFromSearch(null);
              setIsAddOpen(true);
            }}
            className="flex items-center gap-2"
          >
            <Plus className="w-4 h-4" /> Add Game
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setIsSearchOpen(true)}
            className="flex items-center gap-2"
          >
            <Search className="w-4 h-4" /> Search Steam
          </Button>
        </div>
      )}

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4">
        {loadingGames ? (
          <>
            {Array.from({ length: 12 }).map((_, i) => (
              <SkeletonCard key={i} />
            ))}
          </>
        ) : steamGames.length === 0 ? (
          <div className="col-span-full text-center py-16">
            <Monitor className="w-12 h-12 mx-auto text-muted-foreground/30 mb-4" />
            <p className="text-muted-foreground text-lg">
              No Steam games found.
            </p>
            {isAdmin && (
              <Button
                onClick={() => {
                  setAddFromSearch(null);
                  setIsAddOpen(true);
                }}
                className="mt-4"
              >
                <Plus className="w-4 h-4 mr-2" /> Add Game
              </Button>
            )}
          </div>
        ) : (
          steamGames.map((game) => <SteamGameCard key={game.id} game={game} />)
        )}
      </div>

      <SteamGamePopup
        isOpen={isAddOpen}
        onClose={() => {
          setIsAddOpen(false);
          setAddFromSearch(null);
        }}
        game={addFromSearch ? ({ ...addFromSearch } as SteamGame) : undefined}
      />

      <SteamSearchDialog
        isOpen={isSearchOpen}
        onClose={() => setIsSearchOpen(false)}
        onSelect={handleSearchSelect}
      />
    </div>
  );
};

export default SteamGamesTab;
