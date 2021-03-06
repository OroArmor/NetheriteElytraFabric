package com.oroarmor.netherite_plus.mixin.better_end;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.oroarmor.netherite_plus.screen.NetheriteAnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

import ru.betterend.interfaces.AnvilScreenHandlerExtended;
import ru.betterend.recipe.builders.AnvilRecipe;

@Mixin(NetheriteAnvilScreenHandler.class)
public abstract class NetheriteAnvilScreenHandlerMixin extends ForgingScreenHandler implements AnvilScreenHandlerExtended {

    private List<AnvilRecipe> be_recipes = Collections.emptyList();
    private AnvilRecipe be_currentRecipe;

    public NetheriteAnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
                                   ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Shadow
    public abstract void updateResult();

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void be_canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> info) {
        if (this.be_currentRecipe != null) {
            ItemStack output = this.be_currentRecipe.craft(this.input, player);
            if (!output.isEmpty()) {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void be_onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        if (this.be_currentRecipe != null) {
            this.input.getStack(0).decrement(1);
            this.onContentChanged(this.input);
            info.setReturnValue(stack);
        }
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void be_updateOutput(CallbackInfo info) {
        RecipeManager recipeManager = this.player.world.getRecipeManager();
        this.be_recipes = recipeManager.getAllMatches(AnvilRecipe.TYPE, this.input, this.player.world);
        if (this.be_recipes.size() > 0) {
            this.be_currentRecipe = recipeManager.getFirstMatch(AnvilRecipe.TYPE, this.input, this.player.world).get();
            this.be_updateResult();
            info.cancel();
        }

    }

    @Inject(method = "setNewItemName", at = @At("HEAD"), cancellable = true, remap = false)
    public void be_setNewItemName(String string, CallbackInfo info) {
        if (be_currentRecipe != null) {
            info.cancel();
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == 0) {
            this.be_previousRecipe();
            return true;
        } else if (id == 1) {
            this.be_nextRecipe();
            return true;
        }
        return super.onButtonClick(player, id);
    }

    private void be_updateResult() {
        if (be_currentRecipe == null) return;
        this.output.setStack(0, be_currentRecipe.craft(input));
        this.sendContentUpdates();
    }

    @Override
    public void be_updateCurrentRecipe(AnvilRecipe recipe) {
        this.be_currentRecipe = recipe;
        this.be_updateResult();
    }

    @Override
    public AnvilRecipe be_getCurrentRecipe() {
        return this.be_currentRecipe;
    }

    @Override
    public List<AnvilRecipe> be_getRecipes() {
        return this.be_recipes;
    }
}
