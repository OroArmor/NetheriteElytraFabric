/*
 * MIT License
 *
 * Copyright (c) 2021 OroArmor (Eli Orona)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.oroarmor.netherite_plus.client;

import com.oroarmor.netherite_plus.config.NetheritePlusConfig;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static com.oroarmor.netherite_plus.item.NetheritePlusItems.*;

public class NetheritePlusModelProvider {
	private static void registerBowModels() {
		FabricModelPredicateProviderRegistry.register(NETHERITE_BOW.get(), new Identifier("pull"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
		});

		FabricModelPredicateProviderRegistry.register(NETHERITE_BOW.get(), new Identifier("pulling"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}

	private static void registerCrossbowModels() {
		FabricModelPredicateProviderRegistry.register(NETHERITE_CROSSBOW.get(), new Identifier("pull"), (itemStack4, clientWorld3, livingEntity2) -> {
			if (livingEntity2 == null) {
				return 0.0F;
			}
			return CrossbowItem.isCharged(itemStack4) ? 0.0F : (float) (itemStack4.getMaxUseTime() - livingEntity2.getItemUseTimeLeft()) / (float) CrossbowItem.getPullTime(itemStack4);
		});
		FabricModelPredicateProviderRegistry.register(NETHERITE_CROSSBOW.get(), new Identifier("pulling"), (itemStack3, clientWorld1, livingEntity1) -> {
			if (livingEntity1 == null) {
				return 0.0F;
			}
			return livingEntity1.isUsingItem() && livingEntity1.getActiveItem() == itemStack3 && !CrossbowItem.isCharged(itemStack3) ? 1.0F : 0.0F;
		});
		FabricModelPredicateProviderRegistry.register(NETHERITE_CROSSBOW.get(), new Identifier("charged"), (itemStack2, clientWorld4, livingEntity3) -> {
			if (livingEntity3 == null) {
				return 0.0F;
			}
			return CrossbowItem.isCharged(itemStack2) ? 1.0F : 0.0F;
		});

		FabricModelPredicateProviderRegistry.register(NETHERITE_CROSSBOW.get(), new Identifier("firework"), (itemStack1, clientWorld2, livingEntity4) -> {
			if (livingEntity4 == null) {
				return 0.0F;
			}
			return CrossbowItem.isCharged(itemStack1) && CrossbowItem.hasProjectile(itemStack1, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
		});
	}

	private static void registerElytraModels() {
		FabricModelPredicateProviderRegistry.register(NETHERITE_ELYTRA.get(), new Identifier("broken"), (itemStack, clientWorld, livingEntity) -> {
			return ElytraItem.isUsable(itemStack) ? 0.0F : 1.0F;
		});
	}

	private static void registerFishingRodModels() {
		FabricModelPredicateProviderRegistry.register(NETHERITE_FISHING_ROD.get(), new Identifier("cast"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			}
			boolean bl = livingEntity.getMainHandStack() == itemStack;
			boolean bl2 = livingEntity.getOffHandStack() == itemStack;
			if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
				bl2 = false;
			}

			return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).fishHook != null ? 1.0F : 0.0F;
		});
	}

	public static void registerItemsWithModelProvider() {
		if (NetheritePlusConfig.ENABLED.ENABLED_ELYTRA.getValue()) {
			registerElytraModels();
		}

		if (NetheritePlusConfig.ENABLED.ENABLED_SHIELDS.getValue()) {
			registerShieldModels();
		}

		if (NetheritePlusConfig.ENABLED.ENABLED_FISHING_ROD.getValue()) {
			registerFishingRodModels();
		}

		if (NetheritePlusConfig.ENABLED.ENABLED_BOWS_AND_CROSSBOWS.getValue()) {
			registerBowModels();
			registerCrossbowModels();
		}

		if (NetheritePlusConfig.ENABLED.ENABLED_TRIDENT.getValue()) {
			registerTridentModels();
		}
	}

	private static void registerShieldModels() {
		FabricModelPredicateProviderRegistry.register(NETHERITE_SHIELD.get(), new Identifier("blocking"), (itemStack, clientWorld, livingEntity) -> {
			return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}

	private static void registerTridentModels() {
		FabricModelPredicateProviderRegistry.register(NETHERITE_TRIDENT.get(), new Identifier("throwing"), (itemStack, clientWorld, livingEntity) -> {
			return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}
}
