package pl.jewusiak.grzesbankapi.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.jewusiak.grzesbankapi.model.domain.PasswordCombination;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.response.PasswordCombinationResponse;
import pl.jewusiak.grzesbankapi.repository.UserRepository;

import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PasswordCombinationsGenerator {
    private final PasswordEncoder passwordEncoder;

    public List<PasswordCombination> generatePasswordCombinations(String rawPassword, User user) {
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password has to be >= 8 chars long.");
        }
        List<PasswordCombination> list = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            var pc = new PasswordCombination();
            pc.setIndices(selectIndices(rawPassword.length()));
            var selectedChars = Stream.of(pc.getIndices())
                    .map(rawPassword::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            var hashedPassword = passwordEncoder.encode(selectedChars);
            pc.setPasswordHash(hashedPassword);
            pc.setUser(user);
            list.add(pc);
        }
        return list;
    }

    private Integer[] selectIndices(int length) {
        List<Integer> indices = new ArrayList<>(5);
        Random random = new Random();
        while (indices.size() < 5) {
            var index = random.nextInt(length);
            if (!indices.contains(index)) {
                indices.add(index);
            }
        }
        return indices.stream().mapToInt(i -> i).sorted().boxed().toArray(Integer[]::new);
    }

    public PasswordCombinationResponse getRandomPasswordCombination(Optional<User> user) {
        if (user.isEmpty()) {
            // return dummy password combination response with password between 8 and 16 chars
            return PasswordCombinationResponse.builder().pcid(UUID.randomUUID()).indices(selectIndices(new Random().nextInt(8, 17))).build();
        }
        List<PasswordCombination> passwordCombinations = user.get().getPasswordCombinations();
        var pc = passwordCombinations.get(new Random().nextInt(passwordCombinations.size()));
        return PasswordCombinationResponse.builder().pcid(pc.getId()).indices(pc.getIndices()).build();
    }
}
