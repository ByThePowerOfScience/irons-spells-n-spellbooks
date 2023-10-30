package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.capabilities.magic.UpgradeData;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Default priority is 1000
@Mixin(Item.class)
public abstract class ItemMixin {

    /**
    Necessary to display how many times a piece of gear has been upgraded on its name
     */
    @Inject(method = "getName", at = @At("TAIL"), cancellable = true)
    public void getHoverName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        //IronsSpellbooks.LOGGER.info("{}", cir.getReturnValue().getString());
        if (UpgradeData.hasUpgradeData(stack)) {
            cir.setReturnValue(Component.translatable("tooltip.irons_spellbooks.upgrade_plus_format", cir.getReturnValue(), UpgradeData.getUpgradeData(stack).getCount()));
        }
    }

    /**
     * Necessary for any item to be able to have a use duration for being imbued with a continuous cast
     */
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!SpellData.getSpellData(stack).equals(SpellData.EMPTY)) {
            cir.setReturnValue(7200);
        }
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        if (!SpellData.getSpellData(stack).equals(SpellData.EMPTY)) {
            cir.setReturnValue(UseAnim.BOW);
        }
    }
}
