package com.example.nadi.model

import kotlin.math.pow

data class Swara(
    val name: String,
    val notation: String,
    val ratio: Double,
    val fullName: String = ""
) {
    /**
     * Returns a new Swara shifted by the specified number of octaves.
     * For example, octaveOffset = 1 doubles the frequency ratio (Upper octave).
     * octaveOffset = -1 halves the frequency ratio (Lower octave).
     */
    fun octave(octaveOffset: Int): Swara {
        val suffix = when {
            octaveOffset > 0 -> "'".repeat(octaveOffset)
            octaveOffset < 0 -> ",".repeat(-octaveOffset)
            else -> ""
        }
        return Swara(
            name = "$name$suffix",
            notation = "$notation$suffix",
            ratio = ratio * 2.0.pow(octaveOffset.toDouble()),
            fullName = fullName
        )
    }

    companion object {
        val S = Swara("S", "S", 1.0, "Sa")
        val R1 = Swara("R1", "R₁", 256.0 / 243.0, "Ri")
        val R2 = Swara("R2", "R₂", 9.0 / 8.0, "Ri")
        val G1 = Swara("G1", "G₁", 9.0 / 8.0, "Ga")
        val G2 = Swara("G2", "G₂", 32.0 / 27.0, "Ga")
        val G3 = Swara("G3", "G₃", 5.0 / 4.0, "Ga")
        val M1 = Swara("M1", "M₁", 4.0 / 3.0, "Ma")
        val M2 = Swara("M2", "M₂", 45.0 / 32.0, "Ma")
        val P = Swara("P", "P", 3.0 / 2.0, "Pa")
        val D1 = Swara("D1", "D₁", 128.0 / 81.0, "Da")
        val D2 = Swara("D2", "D₂", 27.0 / 16.0, "Da")
        val N1 = Swara("N1", "N₁", 27.0 / 16.0, "Ni")
        val N2 = Swara("N2", "N₂", 16.0 / 9.0, "Ni")
        val N3 = Swara("N3", "N₃", 15.0 / 8.0, "Ni")
        
        // Convenience for upper Sa
        val S_UPPER = Swara("S'", "S'", 2.0, "Sa")
        
        val values = listOf(S, R1, R2, G1, G2, G3, M1, M2, P, D1, D2, N1, N2, N3)
    }
}
