/** 
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.minecraft.src.buildcraft.builders;

import java.util.ArrayList;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_BuildCraftBuilders;
import net.minecraft.src.buildcraft.api.APIProxy;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.tools.IToolWrench;
import net.minecraft.src.buildcraft.core.DefaultProps;
import net.minecraft.src.buildcraft.core.GuiIds;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.forge.ITextureProvider;

public class BlockArchitect extends BlockContainer implements ITextureProvider {

	int blockTextureSides;
	int blockTextureFront;
	int blockTextureTopPos;
	int blockTextureTopNeg;
	int blockTextureTopArchitect;

	public BlockArchitect(int i) {
		super(i, Material.iron);
		setHardness(0.5F);
		blockTextureSides = 3 * 16 + 0;
		blockTextureTopNeg = 3 * 16 + 1;
		blockTextureTopPos = 3 * 16 + 2;
		blockTextureTopArchitect = 3 * 16 + 3;
		blockTextureFront = 3 * 16 + 4;
	}

	@Override
	public String getTextureFile() {
		return DefaultProps.TEXTURE_BLOCKS;
	}

	@Override
	public TileEntity getBlockEntity() {
		return new TileArchitect();
	}

	@Override
	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking())
			return false;

		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, i, j, k)) {

			int meta = world.getBlockMetadata(i, j, k);

			switch (Orientations.values()[meta]) {
			case XNeg:
				world.setBlockMetadata(i, j, k, Orientations.ZPos.ordinal());
				break;
			case XPos:
				world.setBlockMetadata(i, j, k, Orientations.ZNeg.ordinal());
				break;
			case ZNeg:
				world.setBlockMetadata(i, j, k, Orientations.XNeg.ordinal());
				break;
			case ZPos:
				world.setBlockMetadata(i, j, k, Orientations.XPos.ordinal());
				break;
			}

			world.markBlockNeedsUpdate(i, j, k);
			((IToolWrench) equipped).wrenchUsed(entityplayer, i, j, k);
			return true;
		} else {

			if (!APIProxy.isClient(world))
				entityplayer.openGui(mod_BuildCraftBuilders.instance, GuiIds.ARCHITECT_TABLE, world, i, j, k);
			return true;

		}
	}

	@Override
	public void onBlockRemoval(World world, int i, int j, int k) {
		Utils.preDestroyBlock(world, i, j, k);

		super.onBlockRemoval(world, i, j, k);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		super.onBlockPlacedBy(world, i, j, k, entityliving);

		Orientations orientation = Utils.get2dOrientation(new Position(entityliving.posX, entityliving.posY, entityliving.posZ),
				new Position(i, j, k));

		world.setBlockMetadataWithNotify(i, j, k, orientation.reverse().ordinal());
	}

	@SuppressWarnings({ "all" })
	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		int m = iblockaccess.getBlockMetadata(i, j, k);

		if (l == 1) {
			// boolean isPowered = false;
			//
			// if (iblockaccess == null) {
			// return getBlockTextureFromSideAndMetadata(l, m);
			// } else if (iblockaccess instanceof World) {
			// isPowered = ((World) iblockaccess)
			// .isBlockIndirectlyGettingPowered(i, j, k);
			// }
			//
			// if (!isPowered) {
			// return blockTextureTopPos;
			// } else {
			// return blockTextureTopNeg;
			// }

			return blockTextureTopArchitect;
		}

		return getBlockTextureFromSideAndMetadata(l, m);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (j == 0 && i == 3) {
			return blockTextureFront;
		}

		if (i == 1) {
			return blockTextureTopArchitect;
		}

		if (i == j) {
			return blockTextureFront;
		}

		return blockTextureSides;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}
}
