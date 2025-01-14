package io.redspace.ironsspellbooks.item.armor;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ExtendedArmorItem extends ArmorItem implements GeoItem {
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public ExtendedArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, AttributeContainer... attributes) {
        super(pMaterial, pType, pProperties);
        //Shadow of ArmorItem's defaultModifiers. This system is deprecated and will likely change (good)
        this.defaultModifiers = Suppliers.memoize(
                () -> {
                    /////copy start
                    int i = pMaterial.value().getDefense(pType);
                    float f = pMaterial.value().toughness();
                    ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
                    EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(pType.getSlot());
                    ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + pType.getName());
                    builder.add(
                            Attributes.ARMOR, new AttributeModifier(resourcelocation, i, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
                    );
                    builder.add(
                            Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, f, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
                    );
                    float f1 = pMaterial.value().knockbackResistance();
                    if (f1 > 0.0F) {
                        builder.add(
                                Attributes.KNOCKBACK_RESISTANCE,
                                new AttributeModifier(resourcelocation, f1, AttributeModifier.Operation.ADD_VALUE),
                                equipmentslotgroup
                        );
                    }
                    /////copy end
                    for (AttributeContainer holder : attributes) {
                        builder.add(holder.attribute(), holder.createModifier(pType.getSlot().getName()), equipmentslotgroup);
                    }
                    return builder.build();
                }
        );
    }

    public static AttributeContainer[] schoolAttributes(Holder<Attribute> school) {
        return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, 125, AttributeModifier.Operation.ADD_VALUE), new AttributeContainer(school, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)};
    }

    public static AttributeContainer[] withManaAttribute(int mana) {
        return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, mana, AttributeModifier.Operation.ADD_VALUE)};
    }

    public static AttributeContainer[] withManaAndSpellPowerAttribute(int mana, double spellPower) {
        return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, mana, AttributeModifier.Operation.ADD_VALUE), new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)};
    }


    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.defaultModifiers.get();
    }

    /*
    GECKOLIB IMPL
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<ExtendedArmorItem>(this, "controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationState<ExtendedArmorItem> extendedArmorItemAnimationState) {
        extendedArmorItemAnimationState.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = supplyRenderer();
                }
                return this.renderer;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public abstract GeoArmorRenderer<?> supplyRenderer();
}