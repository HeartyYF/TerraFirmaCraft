/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.jei.categories.*;
import net.dries007.tfc.jei.wrappers.AlloyWrapper;
import net.dries007.tfc.jei.wrappers.KnappingWrapper;
import net.dries007.tfc.jei.wrappers.SimpleRecipeWrapper;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLoom;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.metal.ItemAnvil;
import net.dries007.tfc.objects.items.rock.ItemRock;

@JEIPlugin
public final class TFCJEIPlugin implements IModPlugin
{
    private static final String QUERN_UID = TFCConstants.MOD_ID + ".quern";
    private static final String ANVIL_UID = TFCConstants.MOD_ID + ".anvil";
    private static final String WELDING_UID = TFCConstants.MOD_ID + ".welding";
    private static final String PITKILN_UID = TFCConstants.MOD_ID + ".pitkiln";
    private static final String LOOM_UID = TFCConstants.MOD_ID + ".loom";
    private static final String ALLOY_UID = TFCConstants.MOD_ID + ".alloy";
    private static final String KNAP_CLAY_UID = TFCConstants.MOD_ID + ".knap.clay";
    private static final String KNAP_FIRECLAY_UID = TFCConstants.MOD_ID + ".knap.fireclay";
    private static final String KNAP_LEATHER_UID = TFCConstants.MOD_ID + ".knap.leather";
    private static final String KNAP_STONE_UID = TFCConstants.MOD_ID + ".knap.stone";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        //Add new JEI recipe categories
        registry.addRecipeCategories(new QuernCategory(registry.getJeiHelpers().getGuiHelper(), QUERN_UID));
        registry.addRecipeCategories(new AnvilCategory(registry.getJeiHelpers().getGuiHelper(), ANVIL_UID));
        registry.addRecipeCategories(new WeldingCategory(registry.getJeiHelpers().getGuiHelper(), WELDING_UID));
        registry.addRecipeCategories(new PitKilnCategory(registry.getJeiHelpers().getGuiHelper(), PITKILN_UID));
        registry.addRecipeCategories(new LoomCategory(registry.getJeiHelpers().getGuiHelper(), LOOM_UID));
        registry.addRecipeCategories(new AlloyCategory(registry.getJeiHelpers().getGuiHelper(), ALLOY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_CLAY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_FIRECLAY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_LEATHER_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_STONE_UID));
    }

    @Override
    public void register(IModRegistry registry)
    {
        //Wraps all quern recipes
        List<SimpleRecipeWrapper> quernList = TFCRegistries.QUERN.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(quernList, QUERN_UID); //Register recipes to quern category
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.QUERN), QUERN_UID); //Register BlockQuern as the device that do quern recipes

        //Wraps all anvil recipes
        List<SimpleRecipeWrapper> anvilList = TFCRegistries.ANVIL.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(anvilList, ANVIL_UID);

        //Wraps all welding recipes
        List<SimpleRecipeWrapper> weldList = TFCRegistries.WELDING.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(weldList, WELDING_UID);
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (Metal.ItemType.ANVIL.hasType(metal))
            {
                registry.addRecipeCatalyst(new ItemStack(ItemAnvil.get(metal, Metal.ItemType.ANVIL)), ANVIL_UID);
                registry.addRecipeCatalyst(new ItemStack(ItemAnvil.get(metal, Metal.ItemType.ANVIL)), WELDING_UID);
            }
        }

        //Wraps all pit kiln recipes
        List<SimpleRecipeWrapper> pitkilnRecipes = TFCRegistries.PIT_KILN.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(pitkilnRecipes, PITKILN_UID);
        //No "Device" to wrap this to

        //Wraps all loom recipes
        List<SimpleRecipeWrapper> loomRecipes = TFCRegistries.LOOM.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(loomRecipes, LOOM_UID);
        registry.addRecipeCatalyst(new ItemStack(BlockLoom.get(Tree.SEQUOIA)), LOOM_UID);

        //Wraps all alloy recipes
        List<AlloyWrapper> alloyRecipes = TFCRegistries.ALLOYS.getValuesCollection()
            .stream()
            .map(AlloyWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(alloyRecipes, ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CRUCIBLE), ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.CERAMICS_FIRED_VESSEL), ALLOY_UID);

        //Wraps all clay knap recipes
        List<KnappingWrapper> clayknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.CLAY)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(clayknapRecipes, KNAP_CLAY_UID);
        registry.addRecipeCatalyst(new ItemStack(Items.CLAY_BALL), KNAP_CLAY_UID);

        //Wraps all fire clay knap recipes
        List<KnappingWrapper> fireclayknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.FIRE_CLAY)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(fireclayknapRecipes, KNAP_FIRECLAY_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.FIRE_CLAY), KNAP_FIRECLAY_UID);

        //Wraps all leather knap recipes
        List<KnappingWrapper> leatherknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.LEATHER)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(leatherknapRecipes, KNAP_LEATHER_UID);
        registry.addRecipeCatalyst(new ItemStack(Items.LEATHER), KNAP_LEATHER_UID);

        //Wraps all leather knap recipes
        List<KnappingWrapper> stoneknapRecipes = TFCRegistries.KNAPPING.getValuesCollection()
            .stream().filter(recipe -> recipe.getType() == KnappingRecipe.Type.STONE)
            .map(KnappingWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(stoneknapRecipes, KNAP_STONE_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemRock.get(Rock.GRANITE)), KNAP_STONE_UID);
    }
}
