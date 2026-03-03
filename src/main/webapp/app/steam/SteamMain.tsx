import React, { useState, useEffect } from "react";
import { Gamepad2, Monitor, ClipboardList } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { SteamProvider } from "@/steam/SteamManagerContext";
import PageHeader from "@/components/PageHeader";
import SteamGamesTab from "@/steam/SteamGamesTab";
import SteamAccountsTab from "@/steam/SteamAccountsTab";
import LendingRequestsTab from "@/steam/LendingRequestsTab";

type SteamTab = "games" | "accounts" | "requests";

const SteamMainContent: React.FC = () => {
  const { auth } = useAuth();
  const isAdmin = auth?.authenticationLevel.toLowerCase() === "admin";

  const [activeTab, setActiveTab] = useState<SteamTab>("games");

  // Build tab definitions based on permissions
  const tabs: { id: SteamTab; label: string; icon: React.ReactNode }[] = [
    {
      id: "games",
      label: "Games",
      icon: <Gamepad2 className="w-4 h-4" />,
    },
  ];

  if (isAdmin) {
    tabs.push({
      id: "accounts",
      label: "Accounts",
      icon: <Monitor className="w-4 h-4" />,
    });
  }

  tabs.push({
    id: "requests",
    label: "Requests",
    icon: <ClipboardList className="w-4 h-4" />,
  });

  // Reset to "games" if the user doesn't have access to the current tab
  useEffect(() => {
    if (activeTab === "accounts" && !isAdmin) {
      setActiveTab("games");
    }
  }, [activeTab, isAdmin]);

  return (
    <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 py-6">
      <PageHeader
        title="Steam"
        description="Manage Steam games, accounts, and lending requests"
      />

      {/* Tab bar */}
      <div className="flex gap-6 border-b border-border mb-6">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-2 pb-2.5 text-sm font-medium transition-colors border-b-2 -mb-px ${
              activeTab === tab.id
                ? "border-primary text-foreground"
                : "border-transparent text-muted-foreground hover:text-foreground"
            }`}
          >
            {tab.icon}
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab content */}
      {activeTab === "games" && <SteamGamesTab />}
      {activeTab === "accounts" && isAdmin && <SteamAccountsTab />}
      {activeTab === "requests" && <LendingRequestsTab />}
    </div>
  );
};

const SteamMain: React.FC = () => {
  return (
    <SteamProvider>
      <SteamMainContent />
    </SteamProvider>
  );
};

export default SteamMain;
