package com.mattercontrol.net;

import com.mattercontrol.MatterMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

/** Server -> owner: everything the HUD shows. matter < 0 means "not awakened". */
public record SyncPayload(int matter, int max, int selected, int material, int cooldown, int cooldownMax,
                          List<String> palette) implements CustomPayload {
    public static final CustomPayload.Id<SyncPayload> ID = new CustomPayload.Id<>(MatterMod.id("sync"));
    public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC = PacketCodec.of(SyncPayload::write, SyncPayload::read);

    private static void write(SyncPayload p, RegistryByteBuf buf) {
        buf.writeVarInt(p.matter() + 1); // matter < 0 (not awakened) is sent as 0
        buf.writeVarInt(p.max());
        buf.writeVarInt(p.selected());
        buf.writeVarInt(p.material());
        buf.writeVarInt(p.cooldown());
        buf.writeVarInt(p.cooldownMax());
        buf.writeVarInt(p.palette().size());
        for (String s : p.palette()) buf.writeString(s);
    }

    private static SyncPayload read(RegistryByteBuf buf) {
        int matter = buf.readVarInt() - 1;
        int max = buf.readVarInt();
        int selected = buf.readVarInt();
        int material = buf.readVarInt();
        int cooldown = buf.readVarInt();
        int cooldownMax = buf.readVarInt();
        int n = buf.readVarInt();
        List<String> pal = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) pal.add(buf.readString());
        return new SyncPayload(matter, max, selected, material, cooldown, cooldownMax, pal);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
