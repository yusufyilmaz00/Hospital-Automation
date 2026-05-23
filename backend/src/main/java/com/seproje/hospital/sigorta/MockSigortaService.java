package com.seproje.hospital.sigorta;

import org.springframework.stereotype.Service;

@Service
public class MockSigortaService implements SigortaService {

    @Override
    public double indirimOraniSorgula(String tckno) {
        if (tckno == null || tckno.isBlank()) {
            return 0.0;
        }

        int sonRakam = Character.digit(tckno.charAt(tckno.length() - 1), 10);
        return sonRakam >= 0 && sonRakam % 2 == 0 ? 0.20 : 0.0;
    }
}
