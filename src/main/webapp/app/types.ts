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
