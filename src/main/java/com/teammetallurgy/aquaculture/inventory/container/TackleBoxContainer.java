package com.teammetallurgy.aquaculture.inventory.container;

import com.teammetallurgy.aquaculture.api.AquacultureAPI;
import com.teammetallurgy.aquaculture.block.tileentity.TackleBoxTileEntity;
import com.teammetallurgy.aquaculture.init.AquaBlocks;
import com.teammetallurgy.aquaculture.init.AquaGuis;
import com.teammetallurgy.aquaculture.item.AquaFishingRodItem;
import com.teammetallurgy.aquaculture.item.BaitItem;
import com.teammetallurgy.aquaculture.item.HookItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TackleBoxContainer extends Container {
    private TackleBoxTileEntity tackleBox;
    private int rows = 4;
    private int collumns = 4;
    private List<Slot> fishingRodSlots = new ArrayList<>();
    public Slot slotHook;
    public Slot slotBait;
    public Slot slotLine;

    public TackleBoxContainer(int windowID, BlockPos pos, PlayerInventory playerInventory) {
        super(AquaGuis.TACKLE_BOX, windowID);
        this.tackleBox = (TackleBoxTileEntity) playerInventory.player.world.getTileEntity(pos);

        if (this.tackleBox != null) {
            this.tackleBox.openInventory(playerInventory.player);
            this.tackleBox.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                //Fishing Pole
                this.addSlot(new SlotItemHandler(handler, 0, 117, 21) {
                    @Override
                    public boolean isItemValid(@Nonnull ItemStack stack) {
                        Item item = stack.getItem();
                        return item instanceof AquaFishingRodItem;
                    }

                    @Override
                    public void onSlotChanged() {
                        super.onSlotChanged();
                        TackleBoxContainer.this.updateFishingRodSlots(TackleBoxContainer.this.inventorySlots.get(0).getStack());
                    }
                });

                //Fishing Rod slots
                this.updateFishingRodSlots(handler.getStackInSlot(0));

                //Tackle Box
                for (int column = 0; column < collumns; ++column) {
                    for (int row = 0; row < rows; ++row) {
                        this.addSlot(new SlotItemHandler(handler, 1 + row + column * collumns, 8 + row * 18, 8 + column * 18) {
                            @Override
                            public boolean isItemValid(@Nonnull ItemStack stack) {
                                Item item = stack.getItem();
                                return item.isIn(AquacultureAPI.Tags.TACKLE_BOX) || item instanceof HookItem || item instanceof BaitItem || item.isIn(AquacultureAPI.Tags.FISHING_LINE);
                            }
                        });
                    }
                }
            });
        }

        //Player Inventory
        for (int column = 0; column < 3; ++column) {
            for (int row = 0; row < 9; ++row) {
                this.addSlot(new Slot(playerInventory, row + column * 9 + 9, 8 + row * 18, 90 + column * 18));
            }
        }

        //Hotbar
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInventory, row, 8 + row * 18, 148));
        }
    }

    private void updateFishingRodSlots(@Nonnull ItemStack fishingRod) {
        if (!fishingRod.isEmpty()) {
            boolean hasFishinRodSlots = false;
            for (Slot slot : this.fishingRodSlots) {
                if (this.inventorySlots.contains(slot)) {
                    hasFishinRodSlots = true;
                    break;
                }
            }
            if (!hasFishinRodSlots) {
                fishingRod.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(rodHandler -> {
                    //Hook
                    this.fishingRodSlots.add(new SlotItemHandler(rodHandler, 0, 96, 44) {
                        @Override
                        public boolean isItemValid(@Nonnull ItemStack stack) {
                            return stack.getItem() instanceof HookItem;
                        }
                    });
                    this.slotHook = this.fishingRodSlots.get(0);
                    this.addSlot(this.slotHook);

                    //Bait
                    this.fishingRodSlots.add(new SlotItemHandler(rodHandler, 1, 117, 44) {
                        @Override
                        public boolean isItemValid(@Nonnull ItemStack stack) {
                            return stack.getItem() instanceof BaitItem;
                        }

                        @Override
                        public boolean canTakeStack(PlayerEntity player) {
                            return false;
                        }
                    });
                    this.slotBait = this.fishingRodSlots.get(1);
                    this.addSlot(this.slotBait);

                    //Fishing Line
                    this.fishingRodSlots.add(new SlotItemHandler(rodHandler, 2, 138, 44) {
                        @Override
                        public boolean isItemValid(@Nonnull ItemStack stack) {
                            Item item = stack.getItem();
                            return item.isIn(AquacultureAPI.Tags.FISHING_LINE) && item instanceof IDyeableArmorItem;
                        }
                    });
                    this.slotLine = this.fishingRodSlots.get(2);
                    this.addSlot(this.slotLine);
                });
            }
        } else {
            if (this.slotHook != null) {
                this.inventorySlots.remove(this.slotHook);
                this.slotHook = null;
            }
            if (this.slotBait != null) {
                this.inventorySlots.remove(this.slotBait);
                this.slotBait = null;
            }
            if (this.slotLine != null) {
                this.inventorySlots.remove(this.slotLine);
                this.slotLine = null;
            }
        }
        for (int i = 0; i < this.inventorySlots.size(); i++) {
            this.inventorySlots.get(i).slotNumber = i;
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(Objects.requireNonNull(tackleBox.getWorld()), tackleBox.getPos()), player, AquaBlocks.TACKLE_BOX);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack transferStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            transferStack = slotStack.copy();
            if (index < this.rows * this.collumns) {
                if (!this.mergeItemStack(slotStack, this.rows * this.collumns, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 0, this.rows * this.collumns, false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return transferStack;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);
        if (this.tackleBox != null) {
            this.tackleBox.closeInventory(player);
        }
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        //Bait replacing
        if (slotId >= 0 && clickType == ClickType.PICKUP) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot instanceof SlotItemHandler) {
                SlotItemHandler slotHandler = (SlotItemHandler) slot;
                if (slotHandler.getItemHandler().getSlotLimit(1) == 1 && slotHandler.slotNumber == 2) {
                    if (slotHandler.isItemValid(player.inventory.getItemStack())) {
                        slotHandler.putStack(ItemStack.EMPTY); //Set to empty, to allow new bait to get put in
                    }
                }
            }
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }
}