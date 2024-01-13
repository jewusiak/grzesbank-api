package pl.jewusiak.grzesbankapi.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.jewusiak.grzesbankapi.model.domain.PasswordCombination;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.response.PasswordCombinationResponse;

import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordCombinationsGenerator {
    private final PasswordEncoder passwordEncoder;

    private static final double COMB_LEN_COEFF = 0.625;
    private static final int COMB_CNT = 56;

    public List<PasswordCombination> generatePasswordCombinations(String rawPassword) {
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password has to be >= 8 chars long.");
        }
        List<PasswordCombination> list = new ArrayList<>(COMB_CNT);
        
        int combLength = (int) (rawPassword.length() * COMB_LEN_COEFF);
        log.info("Will generate PCs of {} chars", combLength);
        
        int nit = 0;
        for (int i = 0; i < COMB_CNT; i++, nit++) {
            var pc = new PasswordCombination();
            pc.setIndices(selectIndices(rawPassword.length(), combLength));
            if (list.contains(pc)) {
                log.info("List already contains PC no. {}, retrying...", i + 1);
                i--;
                continue;
            }
            var selectedChars = Stream.of(pc.getIndices())
                    .map(rawPassword::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            var hashedPassword = passwordEncoder.encode(selectedChars);
            pc.setPasswordHash(hashedPassword);
            list.add(pc);
            log.info("Generating pass combination no. {}", i + 1);
        }
        log.info("Created PC array in {} iterations.", nit);
        return list;
    }

    private Integer[] selectIndices(int rawPassLen, int combLength) {
        List<Integer> indices = new ArrayList<>(combLength);
        Random random = new Random();
        while (indices.size() < combLength) {
            var index = random.nextInt(rawPassLen);
            if (!indices.contains(index)) {
                indices.add(index);
            }
        }
        return indices.stream().mapToInt(i -> i).sorted().boxed().toArray(Integer[]::new);
    }

    public PasswordCombinationResponse getRandomPasswordCombination(Optional<User> user) {
        if (user.isEmpty()) {
            // return dummy password combination response with password between 8 and 20 chars
            int length = new Random().nextInt(8, 21);
            return PasswordCombinationResponse.builder().pcid(UUID.randomUUID()).indices(selectIndices(length, (int) (length * COMB_LEN_COEFF))).build();
        }
        List<PasswordCombination> passwordCombinations = user.get().getPasswordCombinations();
        var pc = passwordCombinations.get(new Random().nextInt(passwordCombinations.size()));
        return PasswordCombinationResponse.builder().pcid(pc.getId()).indices(pc.getIndices()).build();
    }
}
