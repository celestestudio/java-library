package com.celeste.library.spigot.util.item;

import com.celeste.library.spigot.util.ReflectionNms;
import com.celeste.library.spigot.util.item.type.EnchantmentType;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class ItemBuilder implements Cloneable {

  private final ItemStack itemStack;
  private ItemMeta meta;

  /**
   * Creates a new ItemBuilder with that Material.
   *
   * @param material Material
   */
  public ItemBuilder(final Material material) {
    this.itemStack = new ItemStack(material);
    this.meta = itemStack.getItemMeta();
  }

  /**
   * Creates a new ItemBuilder with that Material and amount
   *
   * @param material Material
   * @param amount   int
   */
  public ItemBuilder(final Material material, final int amount) {
    this.itemStack = new ItemStack(material, amount);
    this.meta = itemStack.getItemMeta();
  }

  /**
   * Creates a new ItemBuilder with that Material, amount and data
   *
   * <p>This is only used in legacy versions.</p>
   *
   * @param material Material
   * @param amount   int
   */
  public ItemBuilder(final Material material, final int amount, final int data) {
    this.itemStack = new ItemStack(material, amount, (short) data);
    this.meta = itemStack.getItemMeta();
  }

  /**
   * Creates a new ItemBuilder with ItemStack
   *
   * @param itemStack ItemStack
   */
  public ItemBuilder(final ItemStack itemStack) {
    this.itemStack = itemStack;
    this.meta = itemStack.getItemMeta();
  }

  /**
   * Sets the ItemBuilder material to that Material
   *
   * @param material Material
   * @return ItemBuilder
   */
  public ItemBuilder material(final Material material) {
    itemStack.setType(material);
    return this;
  }

  /**
   * Sets the ItemBuilder amount to that Integer
   *
   * @param amount int
   * @return ItemBuilder
   */
  public ItemBuilder amount(final int amount) {
    itemStack.setAmount(amount);
    return this;
  }

  /**
   * Sets the ItemBuilder name to that String
   *
   * @param name String
   * @return ItemBuilder
   */
  public ItemBuilder name(final String name) {
    if (!name.equals("")) {
      meta.setDisplayName(name);
    }

    return this;
  }

  /**
   * Sets the ItemBuilder lore to that String
   *
   * @param lore String...
   * @return ItemBuilder
   */
  public ItemBuilder lore(final String... lore) {
    return lore(ImmutableList.copyOf(lore));
  }

  /**
   * Sets the ItemBuilder lore to that List
   *
   * @param lore List
   * @return ItemBuilder
   */
  public ItemBuilder lore(final List<String> lore) {
    meta.setLore(lore);
    return this;
  }

  /**
   * Add lore from that String
   *
   * @param lore String...
   * @return ItemBuilder
   */
  public ItemBuilder addLore(final String... lore) {
    return addLore(ImmutableList.copyOf(lore));
  }

  /**
   * Add lore from that List
   *
   * @param lore List
   * @return ItemBuilder
   */
  public ItemBuilder addLore(final List<String> lore) {
    final List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

    newLore.addAll(lore);
    meta.setLore(newLore);

    return this;
  }

  /**
   * Removes that line of the lore
   *
   * @param index int
   * @return ItemBuilder
   */
  public ItemBuilder removeLore(final int index) {
    final List<String> newLore = meta.getLore();
    if (newLore == null) {
      return this;
    }

    newLore.remove(index);
    meta.setLore(newLore);

    return this;
  }

  /**
   * Replaces that line of lore
   *
   * @param lore  String
   * @param index int
   * @return ItemBuilder
   */
  public ItemBuilder replaceLore(final String lore, final int index) {
    final List<String> newLore = meta.getLore();
    if (newLore == null) {
      return this;
    }

    newLore.set(index, lore);
    meta.setLore(newLore);

    return this;
  }

  /**
   * Replaces the lore
   *
   * @param lore  String
   * @param index int
   * @return ItemBuilder
   */
  public ItemBuilder replaceLoreAnyway(final String lore, final int index) {
    final List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

    if (index >= newLore.size()) {
      for (int i = newLore.size(); i <= index; i++) {
        newLore.add("§c");
      }
    }

    newLore.set(index, lore);
    meta.setLore(newLore);

    return this;
  }

  /**
   * Add a enchantment to the item
   *
   * @param enchantment String
   * @param level       int
   * @return ItemBuilder
   */
  public ItemBuilder enchantment(final String enchantment, final int level) {
    final Enchantment enchant = EnchantmentType.getRealEnchantment(enchantment);
    itemStack.addUnsafeEnchantment(enchant, level);

    return this;
  }

  /**
   * Adds enchantments to the item
   *
   * @param enchantment String...
   * @return ItemBuilder
   */
  public ItemBuilder enchantment(final String... enchantment) {
    return enchantment(ImmutableList.copyOf(enchantment));
  }

  /**
   * Adds enchantments to the item
   *
   * @param enchantment List
   * @return ItemBuilder
   */
  public ItemBuilder enchantment(final List<String> enchantment) {
    enchantment.forEach(enchantAndLevel -> {
      final String[] split = enchantAndLevel.split(":");

      if (split.length != 2) {
        return;
      }

      final Enchantment enchant = EnchantmentType.getRealEnchantment(split[0]);
      final int level = Integer.parseInt(split[1]);
      itemStack.addUnsafeEnchantment(enchant, level);
    });

    return this;
  }

  /**
   * Adds enchantments to the item
   *
   * @param enchantment Entry of String and Integer
   * @return ItemBuilder
   */
  @SafeVarargs
  public final ItemBuilder enchantment(final Entry<String, Integer>... enchantment) {
    Arrays.stream(enchantment).forEach(entry -> {
      final Enchantment enchant = EnchantmentType.getRealEnchantment(entry.getKey());
      final int level = entry.getValue();

      itemStack.addUnsafeEnchantment(enchant, level);
    });

    return this;
  }

  /**
   * Removes the enchantments with those names
   *
   * @param enchantments String...
   * @return ItemBuilder
   */
  public ItemBuilder removeEnchantment(final String... enchantments) {
    Arrays.stream(enchantments).forEach(enchantName -> {
      final Enchantment enchant = EnchantmentType.getRealEnchantment(enchantName);
      itemStack.removeEnchantment(enchant);
    });

    return this;
  }

  /**
   * Sets the item to glow
   *
   * @param glow boolean
   * @return ItemBuilder
   */
  public ItemBuilder glow(final boolean glow) {
    if (glow) {
      itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    if (itemStack.containsEnchantment(Enchantment.DURABILITY)) {
      itemStack.removeEnchantment(Enchantment.DURABILITY);
      meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    return this;
  }

  /**
   * Gets the skull from the URL
   *
   * @param url  String
   * @param uuid UUID
   * @return ItemBuilder
   */
  @SneakyThrows
  public ItemBuilder skull(final String url, final UUID uuid) {
    if (!(meta instanceof SkullMeta)) {
      return this;
    }
    itemStack.setItemMeta(meta);

    final SkullMeta skullMeta = (SkullMeta) meta;
    final String texture = "http://textures.minecraft.net/texture/" + url;

    final Class<?> gameProfileClass = ReflectionNms.getClazz("com.mojang.authlib.GameProfile");
    final Class<?> propertyClass = ReflectionNms.getClazz("com.mojang.authlib.properties.Property");

    final Constructor<?> gameProfileConstructor = ReflectionNms
        .getConstructor(gameProfileClass, UUID.class, String.class);
    final Constructor<?> propertyConstructor = ReflectionNms
        .getConstructor(propertyClass, String.class, String.class);
    final Field propertiesField = ReflectionNms.getDcField(gameProfileClass, "properties");

    final String encoded = Base64.getEncoder().encodeToString(
        String.format("{textures:{SKIN:{url:\"%s\"}}}",
            new Object[]{texture}).getBytes()
    );
    final Object profile = ReflectionNms.instance(gameProfileConstructor, uuid, null);
    final Object property = ReflectionNms.instance(propertyConstructor, "textures", encoded);

    final Class<?> propertiesClass = ReflectionNms.getClazz(propertiesField);

    final Method put = ReflectionNms.getMethod(propertiesClass, "put", Object.class, Object.class);
    final Object properties = ReflectionNms.get(propertiesField, profile);

    ReflectionNms.invoke(put, properties, "textures", property);

    final Field profileField = ReflectionNms.getDcField(meta.getClass(), "profile");
    profileField.set(skullMeta, profile);

    meta = skullMeta;
    return this;
  }

  /**
   * Sets the skull of the item
   *
   * @param owner OfflinePlayer
   * @return ItemBuilder
   */
  public ItemBuilder skull(final String owner) {
    final SkullMeta meta = (SkullMeta) this.meta;
    meta.setOwner(owner);

    return this;
  }

  /**
   * Sets the mob inside the spawner
   *
   * @param type EntityType
   * @return ItemBuilder
   */
  public ItemBuilder mob(final EntityType type) {
    if (!(meta instanceof BlockStateMeta)) {
      return this;
    }

    final BlockState blockState = ((BlockStateMeta) meta).getBlockState();
    ((CreatureSpawner) blockState).setSpawnedType(type);
    ((BlockStateMeta) meta).setBlockState(blockState);

    return this;
  }

  /**
   * Sets the color of the armor
   *
   * @param color Color
   * @return ItemBuilder
   */
  public ItemBuilder armor(final Color color) {
    if (!(meta instanceof LeatherArmorMeta)) {
      return this;
    }

    final LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
    armorMeta.setColor(color);

    return this;
  }

  /**
   * Creates a potion
   *
   * @param potions List
   * @return ItemBuilder
   */
  public ItemBuilder potion(final List<String> potions) {
    if (!(meta instanceof PotionMeta)) {
      return this;
    }

    potions.stream()
        .map(potionNameDurationAndAmplifier -> potionNameDurationAndAmplifier.split(":"))
        .filter(split -> split.length == 3 && (PotionEffectType.getByName(split[0]) != null
            || (Pattern.matches("[0-9]+", split[0])
            && PotionEffectType.getById(Integer.parseInt(split[0])) != null)))
        .forEach(split -> {
          final String potionName = split[0];
          final int duration = Integer.parseInt(split[1]);
          final int amplifier = Integer.parseInt(split[2]);

          final PotionMeta potionMeta = (PotionMeta) meta;
          final PotionEffectType type = Pattern.matches("[0-9]+", potionName)
              ? PotionEffectType.getById(Integer.parseInt(potionName))
              : PotionEffectType.getByName(potionName);

          final PotionEffect effect = type.createEffect(duration * 20, amplifier);
          potionMeta.addCustomEffect(effect, true);
        });

    return this;
  }

  /**
   * Creates a potion with the details
   *
   * @param potionName String
   * @param duration   int
   * @param amplifier  int
   * @return ItemBuilder
   */
  public ItemBuilder potion(final String potionName, final int duration,
      final int amplifier) {
    if (!(meta instanceof PotionMeta)) {
      return this;
    }

    final PotionMeta potionMeta = (PotionMeta) meta;
    final PotionEffectType type = Pattern.matches("[0-9]+", potionName)
        ? PotionEffectType.getById(Integer.parseInt(potionName))
        : PotionEffectType.getByName(potionName);

    if (type == null) {
      return this;
    }

    final PotionEffect effect = type.createEffect(duration * 20, amplifier);
    potionMeta.addCustomEffect(effect, true);

    return this;
  }

  /**
   * Removes the potion effect
   *
   * @param potionName String
   * @return ItemBuilder
   */
  public ItemBuilder removePotion(final String potionName) {
    if (!(meta instanceof PotionMeta)) {
      return this;
    }

    final PotionMeta potionMeta = (PotionMeta) meta;
    final PotionEffectType type = Pattern.matches("[0-9]+", potionName)
        ? PotionEffectType.getById(Integer.parseInt(potionName))
        : PotionEffectType.getByName(potionName);

    if (type == null) {
      return this;
    }

    potionMeta.removeCustomEffect(type);
    return this;
  }

  /**
   * Sets the NBT tag
   *
   * @param key   T
   * @param value T
   * @param <T>   T
   * @return ItemBuilder
   */
  @NotNull
  @SneakyThrows
  public <T> ItemBuilder nbtTag(@NotNull final T key, @NotNull final T value) {
    itemStack.setItemMeta(meta);

    final Class<?> craftItemStackClazz = ReflectionNms.getObc("inventory.CraftItemStack");
    final Class<?> itemStackClazz = ReflectionNms.getNms("ItemStack");
    final Class<?> compoundClazz = ReflectionNms.getNms("NBTTagCompound");

    final Method asNmsCopy = ReflectionNms
        .getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
    final Method hasTag = ReflectionNms.getMethod(itemStackClazz, "hasTag");
    final Method getTag = ReflectionNms.getMethod(itemStackClazz, "getTag");

    final Object nmsItem = ReflectionNms.invokeStatic(asNmsCopy, itemStack);
    final boolean isExist = (Boolean) ReflectionNms.invoke(hasTag, nmsItem);
    final Object compound =
        isExist ? ReflectionNms.invoke(getTag, nmsItem) : compoundClazz.newInstance();

    final Class<?> tagClazz = ReflectionNms.getNms("NBTTagString");
    final Class<?> baseClazz = ReflectionNms.getNms("NBTBase");

    final Constructor<?> tagCon = ReflectionNms.getDcConstructor(tagClazz, String.class);

    final Method set = ReflectionNms.getMethod(compoundClazz, "set", String.class, baseClazz);
    final Method setTag = ReflectionNms.getMethod(itemStackClazz, "setTag", compoundClazz);
    final Method getItemMeta = ReflectionNms
        .getMethod(craftItemStackClazz, "getItemMeta", itemStackClazz);

    final Object tag = ReflectionNms.instance(tagCon, value.toString());
    ReflectionNms.invoke(set, compound, key.toString(), tag);
    ReflectionNms.invoke(setTag, nmsItem, compound);

    meta = (ItemMeta) ReflectionNms.invokeStatic(getItemMeta, nmsItem);

    return this;
  }

  /**
   * Sets the ItemFlag into the item
   *
   * @param flag ItemFlag
   * @return ItemBuilder
   */
  @NotNull
  public ItemBuilder flag(@NotNull final ItemFlag... flag) {
    meta.addItemFlags(flag);
    return this;
  }

  /**
   * Removes the ItemFlag from the item
   *
   * @param flag ItemFlag
   * @return ItemBuilder
   */
  @NotNull
  public ItemBuilder removeFlag(@NotNull final ItemFlag... flag) {
    meta.removeItemFlags(flag);
    return this;
  }

  /**
   * Builds the item
   *
   * @return ItemStack
   */
  @NotNull
  public ItemStack build() {
    itemStack.setItemMeta(meta);
    return itemStack;
  }

  /**
   * Clones the ItemBuilder
   *
   * @return ItemBuilder
   */
  @Override
  @NotNull
  @SneakyThrows
  public ItemBuilder clone() {
    return (ItemBuilder) super.clone();
  }

}
