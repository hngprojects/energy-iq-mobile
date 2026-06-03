package com.hng14.energyiq.core.data

/**
 * Curated (UI-friendly) "places" per state for dropdown usage.
 *
 * Notes:
 * - This is intentionally NOT exhaustive (Nigeria has thousands of towns/communities).
 * - Lists are capped to 30 items per state for UX; include "Other" where free-typing is desired.
 */
object NigeriaStateCities {

  // State -> curated places (max 30 per state)
  val stateToCities: Map<String, List<String>> = linkedMapOf(
    "Federal Capital Territory (FCT)" to listOf(
      "Abuja",
      "Central Area (CBD)",
      "Asokoro",
      "Garki",
      "Wuse",
      "Maitama",
      "Jabi",
      "Jahi",
      "Utako",
      "Wuye",
      "Gwarinpa",
      "Life Camp",
      "Kubwa",
      "Nyanya",
      "Karu",
      "Jikwoyi",
      "Lugbe",
      "Gudu",
      "Durumi",
      "Apo",
      "Guzape",
      "Katampe",
      "Mabushi",
      "Mpape",
      "Dutse Alhaji",
      "Zuba",
      "Gwagwalada",
      "Kuje",
      "Bwari",
      "Other",
    ),
    "Lagos" to listOf(
      "Lagos Island",
      "Ikeja",
      "Surulere",
      "Yaba",
      "Lekki",
      "Victoria Island",
      "Ikoyi",
      "Ajah",
      "Epe",
      "Badagry",
      "Ikorodu",
      "Alimosho",
      "Oshodi",
      "Isolo",
      "Mushin",
      "Agege",
      "Ifako-Ijaiye",
      "Shomolu",
      "Kosofe",
      "Eti-Osa",
      "Amuwo-Odofin",
      "Apapa",
      "Ajeromi-Ifelodun",
      "Ojo",
      "Ibeju-Lekki",
      "Maryland",
      "Ogba",
      "Egbeda",
      "Festac",
      "Other",
    ),

    // The rest are intentionally smaller seed lists; "Other" allows typing while we expand.
    "Abia" to listOf("Umuahia", "Aba", "Ohafia", "Arochukwu", "Bende", "Other"),
    "Adamawa" to listOf("Yola", "Jimeta", "Mubi", "Numan", "Ganye", "Other"),
    "Akwa Ibom" to listOf("Uyo", "Ikot Ekpene", "Eket", "Oron", "Ikot Abasi", "Other"),
    "Anambra" to listOf("Awka", "Onitsha", "Nnewi", "Ekwulobia", "Agulu", "Other"),
    "Bauchi" to listOf("Bauchi", "Azare", "Misau", "Jama'are", "Katagum", "Other"),
    "Bayelsa" to listOf("Yenagoa", "Brass", "Ogbia", "Sagbama", "Twon-Brass", "Other"),
    "Benue" to listOf("Makurdi", "Gboko", "Otukpo", "Katsina-Ala", "Vandeikya", "Other"),
    "Borno" to listOf("Maiduguri", "Biu", "Dikwa", "Monguno", "Gwoza", "Other"),
    "Cross River" to listOf("Calabar", "Ikom", "Ogoja", "Ugep", "Obudu", "Other"),
    "Delta" to listOf("Asaba", "Warri", "Sapele", "Ughelli", "Agbor", "Other"),
    "Ebonyi" to listOf("Abakaliki", "Afikpo", "Onueke", "Ishielu", "Ezza", "Other"),
    "Edo" to listOf("Benin City", "Auchi", "Ekpoma", "Uromi", "Igarra", "Other"),
    "Ekiti" to listOf("Ado-Ekiti", "Ikere-Ekiti", "Ijero", "Efon-Alaaye", "Omuo", "Other"),
    "Enugu" to listOf("Enugu", "Nsukka", "Awgu", "Oji River", "Udi", "Other"),
    "Gombe" to listOf("Gombe", "Kumo", "Deba", "Billiri", "Bajoga", "Other"),
    "Imo" to listOf("Owerri", "Orlu", "Okigwe", "Mbaise", "Oguta", "Other"),
    "Jigawa" to listOf("Dutse", "Hadejia", "Gumel", "Kazaure", "Birnin Kudu", "Other"),
    "Kaduna" to listOf("Kaduna", "Zaria", "Kafanchan", "Kagoro", "Saminaka", "Other"),
    "Kano" to listOf("Kano", "Wudil", "Gaya", "Bichi", "Rano", "Other"),
    "Katsina" to listOf("Katsina", "Daura", "Funtua", "Malumfashi", "Dutsin-Ma", "Other"),
    "Kebbi" to listOf("Birnin Kebbi", "Argungu", "Yauri", "Zuru", "Kamba", "Other"),
    "Kogi" to listOf("Lokoja", "Okene", "Idah", "Kabba", "Anyigba", "Other"),
    "Kwara" to listOf("Ilorin", "Offa", "Jebba", "Lafiagi", "Pategi", "Other"),
    "Nasarawa" to listOf("Lafia", "Keffi", "Akwanga", "Nasarawa", "Karu", "Other"),
    "Niger" to listOf("Minna", "Bida", "Suleja", "Kontagora", "Lapai", "Other"),
    "Ogun" to listOf("Abeokuta", "Ijebu-Ode", "Sagamu", "Ota", "Ilaro", "Other"),
    "Ondo" to listOf("Akure", "Ondo", "Owo", "Ikare", "Okitipupa", "Other"),
    "Osun" to listOf("Osogbo", "Ile-Ife", "Ilesa", "Iwo", "Ede", "Other"),
    "Oyo" to listOf("Ibadan", "Ogbomosho", "Oyo", "Iseyin", "Saki", "Other"),
    "Plateau" to listOf("Jos", "Bukuru", "Pankshin", "Shendam", "Wase", "Other"),
    "Rivers" to listOf("Port Harcourt", "Bonny", "Okrika", "Degema", "Ahoada", "Other"),
    "Sokoto" to listOf("Sokoto", "Tambuwal", "Wurno", "Gwadabawa", "Illela", "Other"),
    "Taraba" to listOf("Jalingo", "Wukari", "Bali", "Ibi", "Serti", "Other"),
    "Yobe" to listOf("Damaturu", "Potiskum", "Nguru", "Gashua", "Geidam", "Other"),
    "Zamfara" to listOf("Gusau", "Kaura Namoda", "Talata Mafara", "Anka", "Maru", "Other"),
  )

  val states: List<String> = stateToCities.keys.toList()

  fun citiesFor(state: String): List<String> = stateToCities[state].orEmpty()
}

