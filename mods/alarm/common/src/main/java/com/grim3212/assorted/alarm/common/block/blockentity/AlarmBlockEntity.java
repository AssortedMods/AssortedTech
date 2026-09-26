package com.grim3212.assorted.alarm.common.block.blockentity;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.alarm.common.sounds.AlarmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class AlarmBlockEntity extends BlockEntity {

    private int alarmType = 0;

    public AlarmBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public AlarmBlockEntity(BlockPos pos, BlockState state) {
        super(AlarmBlockEntityTypes.ALARM.get(), pos, state);
    }

    /**
     * Block entity serialization moved off {@code CompoundTag} onto {@link ValueInput} /
     * {@link ValueOutput}, which carry the registry lookup with them. {@code load} became
     * {@code loadAdditional}, and the typed getters take a default rather than returning 0.
     */
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.alarmType = input.getIntOr("AlarmType", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("AlarmType", this.alarmType);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
        this.markUpdated();
    }

    public static IRegistryObject<SoundEvent> getSound(int type) {
        switch (type) {
            case 0:
                return AlarmSounds.ALARM_A;
            case 1:
                return AlarmSounds.ALARM_B;
            case 2:
                return AlarmSounds.ALARM_C;
            case 3:
                return AlarmSounds.ALARM_D;
            case 4:
                return AlarmSounds.ALARM_E;
            case 5:
                return AlarmSounds.ALARM_F;
            case 6:
                return AlarmSounds.ALARM_G;
            case 7:
                return AlarmSounds.ALARM_H;
            case 8:
                return AlarmSounds.ALARM_I;
            case 9:
                return AlarmSounds.ALARM_J;
            case 10:
                return AlarmSounds.ALARM_K;
            case 11:
                return AlarmSounds.ALARM_L;
            case 12:
                return AlarmSounds.ALARM_M;
            default:
                return AlarmSounds.ALARM_N;
        }
    }
}
