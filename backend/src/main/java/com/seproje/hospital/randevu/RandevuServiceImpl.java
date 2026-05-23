package com.seproje.hospital.randevu;

import com.seproje.hospital.randevu.dto.RandevuOdemeDTO;
import com.seproje.hospital.randevu.mapper.RandevuOdemeMapper;
import com.seproje.hospital.sigorta.SigortaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class RandevuServiceImpl implements RandevuService {

    private final RandevuRepository randevuRepository;
    private final SigortaService sigortaService;
    private final RandevuOdemeMapper randevuOdemeMapper;

    @Override
    @Transactional
    public RandevuOdemeDTO ucretHesaplaVeOdemeAl(Long randevuId) {
        Randevu randevu = randevuRepository.findById(randevuId)
                .orElseThrow(() -> new EntityNotFoundException("Randevu bulunamadı: " + randevuId));

        if (Boolean.TRUE.equals(randevu.getOdendi())) {
            throw new IllegalArgumentException("Bu randevunun ödemesi zaten alınmış.");
        }

        double brutUcret = brutUcretHesapla(randevu);
        double indirimOrani = sigortaService.indirimOraniSorgula(randevu.getHasta().getIletisimBilgisi().getTckno());
        double odenecekUcret = yuvarla(brutUcret * (1 - indirimOrani));

        randevu.setSigortaIndirimOrani(indirimOrani);
        randevu.setÜcret(odenecekUcret);
        randevu.setOdendi(true);

        Randevu saved = randevuRepository.save(randevu);
        return randevuOdemeMapper.toDTO(saved, yuvarla(brutUcret));
    }

    private double brutUcretHesapla(Randevu randevu) {
        double doktorSaatUcreti = randevu.getDoktor().getUnvan() == null
                ? 0.0
                : randevu.getDoktor().getUnvan().getHourlyRate();
        double sureSaat = randevu.getSureDakika() == null ? 0.5 : randevu.getSureDakika() / 60.0;
        double muayeneUcreti = doktorSaatUcreti * sureSaat;

        double tedaviUcreti = randevu.getTedaviler().stream()
                .mapToDouble(tedavi -> tedaviUcreti(tedavi.getTedaviTipi()))
                .sum();

        return yuvarla(muayeneUcreti + tedaviUcreti);
    }

    private double tedaviUcreti(TedaviTipi tedaviTipi) {
        if (tedaviTipi == null) {
            return 0.0;
        }

        return switch (tedaviTipi) {
            case AMELİYAT -> 5_000.0;
            case İLAÇ_TEDAVİSİ -> 750.0;
            case FİZİK_TEDAVİ -> 1_500.0;
            case SERVİS_YATIŞ -> 2_000.0;
        };
    }

    private double yuvarla(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
