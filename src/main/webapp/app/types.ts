export interface Game {
  id: number;
  name: string;
  availableCopies?: number;
  checkoutCount?: number;
  genre?: string;
  description?: string;
  minPlayerCount?: number;
  maxPlayerCount?: number;
  minPlaytime?: number;
  maxPlaytime?: number;
  boxImageUrl?: string;
  quantity?: number;
  internalNotes?: string;
  createdAt?: string; // ISO date string from backend
  location?: string;
}

export interface EquipmentItem {
  id: number;
  name: string;
  type?: string; // CONTROLLER, JOYCON, RPG_EQUIPMENT, OTHER
  quantity?: number;
  availableCopies?: number;
  description?: string;
  imageUrl?: string;
  location?: string;
  checkoutCount?: number;
  internalNotes?: string;
  createdAt?: string; // ISO date string from backend
}

export interface SteamAccount {
  id: number;
  steamAccountUsername: string;
  available: boolean;
  notes?: string;
  createdAt?: string;
}

export interface SteamGame {
  id: number;
  name: string;
  description?: string;
  steamAppId?: string;
  imageUrl?: string;
  checkoutCount?: number;
  internalNotes?: string;
  windows?: boolean;
  macos?: boolean;
  linux?: boolean;
  createdAt?: string;
}

export interface SteamAccountRequest {
  id: number;
  status: string; // PENDING, APPROVED, DENIED, RETURNED
  name: string;
  email: string;
  gameName: string;
  comments?: string;
  rentalStartDay?: string;
  rentalEndDay?: string;
  assignedAccount?: SteamAccount;
  createdAt?: string;
}
