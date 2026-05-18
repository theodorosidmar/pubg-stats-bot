package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgkt.mastery.WeaponMastery
import dev.pubgkt.mastery.WeaponSummary
import dev.pubgstats.bot.discord.command.CommandOption.StringOption.Choice

enum class Weapon(
    val label: String,
    val id: String,
    val lookup: (WeaponMastery) -> WeaponSummary?,
) {
    // Assault Rifles
    ACE32("ACE32", "ace32", WeaponMastery::ace32),
    AKM("AKM", "akm", WeaponMastery::ak47),
    AUG("AUG", "aug", WeaponMastery::aug),
    BERYL("Beryl M762", "beryl", WeaponMastery::beryl),
    FAMAS("FAMAS", "famas", WeaponMastery::famas),
    G36("G36C", "g36", WeaponMastery::g36),
    GROZA("Groza", "groza", WeaponMastery::groza),
    K2("K2", "k2", WeaponMastery::k2),
    M16A4("M16A4", "m16a4", WeaponMastery::m16a4),
    M416("M416", "m416", WeaponMastery::m416),
    MUTANT("Mk47 Mutant", "mutant", WeaponMastery::mutant),
    QBZ("QBZ95", "qbz", WeaponMastery::qbz),
    SCAR_L("SCAR-L", "scarl", WeaponMastery::scarl),

    // DMRs
    DRAGUNOV("Dragunov", "dragunov", WeaponMastery::dragunov),
    MINI14("Mini 14", "mini14", WeaponMastery::mini14),
    MK12("Mk12", "mk12", WeaponMastery::mk12),
    MK14("Mk14", "mk14", WeaponMastery::mk14),
    QBU("QBU88", "qbu", WeaponMastery::qbu),
    SKS("SKS", "sks", WeaponMastery::sks),
    SLR("SLR", "slr", WeaponMastery::slr),

    // Sniper Rifles
    AWM("AWM", "awm", WeaponMastery::awm),
    KAR98K("Kar98k", "kar98k", WeaponMastery::kar98k),
    LYNX("Lynx", "lynx", WeaponMastery::lynx),
    M24("M24", "m24", WeaponMastery::m24),
    MOSIN_NAGANT("Mosin-Nagant", "mosinnagant", WeaponMastery::mosinNagant),
    ;

    fun toChoice(): Choice = Choice(label = label, value = id)

    companion object {
        fun forId(id: String): Weapon = Weapon.entries.first { it.id == id }
    }
}
