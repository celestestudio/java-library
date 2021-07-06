package com.celeste.library.spigot.util.message;

import com.celeste.library.core.util.Reflection;
import com.celeste.library.spigot.error.ServerStartError;
import com.celeste.library.spigot.util.ReflectionNms;
import com.celeste.library.spigot.util.message.type.ClickEventType;
import com.celeste.library.spigot.util.message.type.HoverEventType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Json {

  private static final Constructor<?> PACKET_CHAT_CONSTRUCTOR;
  private static final Method A;

  static {
    try {
      final Class<?> packetChatClazz = ReflectionNms.getNms("PacketPlayOutChat");
      final Class<?> componentClazz = ReflectionNms.getNms("IChatBaseComponent");

      final Class<?> serializer = Reflection.getDcClasses(componentClazz).length > 0
          ? Reflection.getDcClasses(componentClazz, 0)
          : ReflectionNms.getNms("ChatSerializer");

      A = Reflection.getMethod(serializer, "a", String.class);

      PACKET_CHAT_CONSTRUCTOR = Reflection.getConstructor(packetChatClazz, componentClazz,
          byte.class);
    } catch (ReflectiveOperationException exception) {
      throw new ServerStartError(exception);
    }
  }

  private String json;
  private String text;

  private String hover;
  private String click;

  private HoverEventType hoverType;
  private ClickEventType clickType;

  public Json build() {
    if (click == null && hover == null) {
      this.json = "{\"text\":\"" + text + "\"}";
      return this;
    }

    if (click != null && hover == null) {
      this.json = "{\"text\":\"" + text + "\",\"clickEvent\":{\"action\":\"" + clickType.getName()
          + "\",\"value\":\"" + click + "\"}}";
      return this;
    }

    if (click == null && hover != null) {
      this.json = "{\"text\":\"" + text + "\",\"hoverEvent\":{\"action\":\"" + hoverType.getName()
          + "\",\"value\":\"" + hover + "\"}}";
      return this;
    }

    this.json = "{\"text\":\"" + text + "\",\"clickEvent\":{\"action\":\"" + clickType.getName()
        + "\",\"value\":\"" + click + "\"},\"hoverEvent\":{\"action\":\"" + hoverType.getName()
        + "\",\"value\":\"" + hover + "\"}}";
    return this;
  }

  @SneakyThrows
  public void send(final Player player) {
    if (json == null) {
      throw new NullPointerException("Json cannot be null, use the build method "
          + "before sending it to players");
    }

    final Object text = Reflection.invokeStatic(A, json);
    final Object packet = Reflection.instance(PACKET_CHAT_CONSTRUCTOR, text, (byte) 1);
    ReflectionNms.sendPacket(player, packet);
  }

  @SneakyThrows
  public void sendAll() {
    if (json == null) {
      throw new NullPointerException("Json cannot be null, use the build method "
          + "before sending it to players");
    }

    final Object text = Reflection.invokeStatic(A, json);
    final Object packet = Reflection.instance(PACKET_CHAT_CONSTRUCTOR, text, (byte) 1);

    for (final Player player : Bukkit.getOnlinePlayers()) {
      ReflectionNms.sendPacket(player, packet);
    }
  }

  public static Json builder() {
    return new Json();
  }

  public Json text(@NotNull final String text) {
    this.text = text;
    return this;
  }

  public Json hover(@NotNull final String hover) {
    this.hover = hover;
    return this;
  }

  public Json click(@NotNull final String click) {
    this.hover = hover;
    return this;
  }

  public Json hoverType(@NotNull final HoverEventType hoverType) {
    this.hoverType = hoverType;
    return this;
  }

  public Json clickType(@NotNull final ClickEventType clickType) {
    this.clickType = clickType;
    return this;
  }

}
