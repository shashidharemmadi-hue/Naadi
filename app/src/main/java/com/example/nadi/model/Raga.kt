package com.example.nadi.model

data class Raga(
    val name: String,
    val arohanam: List<Swara>,
    val avarohanam: List<Swara>
) {
    companion object {
        val Hamsadhwani = Raga(
            "Hamsadhwani",
            listOf(Swara.S, Swara.R2, Swara.G3, Swara.P, Swara.N3, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.N3, Swara.P, Swara.G3, Swara.R2, Swara.S)
        )

        val Mohanam = Raga(
            "Mohanam",
            listOf(Swara.S, Swara.R2, Swara.G3, Swara.P, Swara.D2, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.D2, Swara.P, Swara.G3, Swara.R2, Swara.S)
        )

        val Kalyani = Raga(
            "Kalyani",
            listOf(Swara.S, Swara.R2, Swara.G3, Swara.M2, Swara.P, Swara.D2, Swara.N3, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.N3, Swara.D2, Swara.P, Swara.M2, Swara.G3, Swara.R2, Swara.S)
        )

        val Shankarabharanam = Raga(
            "Shankarabharanam",
            listOf(Swara.S, Swara.R2, Swara.G3, Swara.M1, Swara.P, Swara.D2, Swara.N3, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.N3, Swara.D2, Swara.P, Swara.M1, Swara.G3, Swara.R2, Swara.S)
        )

        val Mayamalavagowla = Raga(
            "Mayamalavagowla",
            listOf(Swara.S, Swara.R1, Swara.G3, Swara.M1, Swara.P, Swara.D1, Swara.N3, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.N3, Swara.D1, Swara.P, Swara.M1, Swara.G3, Swara.R1, Swara.S)
        )

        val Hindolam = Raga(
            "Hindolam",
            listOf(Swara.S, Swara.G2, Swara.M1, Swara.D1, Swara.N2, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.N2, Swara.D1, Swara.M1, Swara.G2, Swara.S)
        )

        val Madhyamavati = Raga(
            "Madhyamavati",
            listOf(Swara.S, Swara.R2, Swara.M1, Swara.P, Swara.N2, Swara.S_UPPER),
            listOf(Swara.S_UPPER, Swara.N2, Swara.P, Swara.M1, Swara.R2, Swara.S)
        )

        val allRagas = listOf(
            Hamsadhwani, Mohanam, Kalyani, Shankarabharanam, Mayamalavagowla, Hindolam, Madhyamavati
        )
    }
}
