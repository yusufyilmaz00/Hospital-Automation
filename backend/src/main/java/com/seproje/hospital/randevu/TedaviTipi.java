package com.seproje.hospital.randevu;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TedaviTipi {

    @JsonProperty("AMELIYAT")
    AMELİYAT,

    @JsonProperty("ILAC_TEDAVISI")
    İLAÇ_TEDAVİSİ,

    @JsonProperty("FIZIK_TEDAVI")
    FİZİK_TEDAVİ,

    @JsonProperty("SERVIS_YATIS")
    SERVİS_YATIŞ
}