/*
 * Copper Kingdom - copper-based weapons and armor with mythic lore mechanics.
 * Copyright (C) 2026 Carmelo Santana
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * See the LICENSE file at the project root for the full license text.
 */
package org.xpfarm.copperkingdom.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.xpfarm.copperkingdom.commands.CopperKingdomCommand.RecipientMode;

/**
 * Pins the recipient-routing decision for {@code /copperkingdom give <item> [player]} and
 * {@code /copperkingdom blessed <weapon> [player]}.
 *
 * <p>The item creation and inventory delivery need a live Bukkit server and are proven at
 * gate 7a over RCON; the branch that changed here -- who receives the item, and whether a
 * console sender is allowed -- is factored into
 * {@link CopperKingdomCommand#recipientMode(boolean, boolean)} so it can be pinned
 * headlessly. Each case below maps to one acceptance requirement.
 */
class CopperKingdomCommandTest {

    @Test
    @DisplayName("self-give: a player with no target argument gives to themselves")
    void playerWithoutTargetGivesToSelf() {
        assertEquals(RecipientMode.SELF, CopperKingdomCommand.recipientMode(true, false));
    }

    @Test
    @DisplayName("give-to-other: a player naming a target resolves that target")
    void playerWithTargetResolvesTarget() {
        assertEquals(RecipientMode.RESOLVE_TARGET, CopperKingdomCommand.recipientMode(true, true));
    }

    @Test
    @DisplayName("console with a target resolves that target")
    void consoleWithTargetResolvesTarget() {
        assertEquals(RecipientMode.RESOLVE_TARGET, CopperKingdomCommand.recipientMode(false, true));
    }

    @Test
    @DisplayName("console without a target is told to specify one (no exception)")
    void consoleWithoutTargetNeedsTarget() {
        assertEquals(RecipientMode.CONSOLE_NEEDS_TARGET, CopperKingdomCommand.recipientMode(false, false));
    }
}
