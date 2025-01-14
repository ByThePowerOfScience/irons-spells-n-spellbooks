package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Default priority is 1000
@Mixin(Item.class)
public abstract class ItemMixin {

    /**
     * Necessary to display how many times a piece of gear has been upgraded on its name
     */
    @Inject(method = "getName", at = @At("TAIL"), cancellable = true)
    public void getHoverName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        //IronsSpellbooks.LOGGER.info("{}", cir.getReturnValue().getString());
        if (stack.has(ComponentRegistry.UPGRADE_DATA)) {
            cir.setReturnValue(Component.translatable("tooltip.irons_spellbooks.upgrade_plus_format", cir.getReturnValue(), stack.get(ComponentRegistry.UPGRADE_DATA).getTotalUpgrades()));
        }
    }
}
