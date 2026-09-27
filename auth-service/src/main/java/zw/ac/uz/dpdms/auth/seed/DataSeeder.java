package zw.ac.uz.dpdms.auth.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import zw.ac.uz.dpdms.auth.domain.Role;
import zw.ac.uz.dpdms.auth.domain.User;
import zw.ac.uz.dpdms.auth.repo.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository repo;
    private final PasswordEncoder enc;

    public DataSeeder(UserRepository repo, PasswordEncoder enc) {
        this.repo = repo;
        this.enc = enc;
    }

    @Override
    public void run(String... args) {
        seed("flood.recorder.wardA",    Role.FLOOD_RECORDER,    "Ward A");
        seed("drought.recorder.wardA",  Role.DROUGHT_RECORDER,  "Ward A");
        seed("fire.recorder.wardB",     Role.FIRE_RECORDER,     "Ward B");
        seed("zoonotic.recorder.wardC", Role.ZOONOTIC_RECORDER, "Ward C");
        seed("mining.recorder.wardD",   Role.MINING_RECORDER,   "Ward D");
        seed("flood.supervisor",        Role.FLOOD_SUPERVISOR,  null);
        seed("drought.supervisor",      Role.DROUGHT_SUPERVISOR,null);
        seed("fire.supervisor",         Role.FIRE_SUPERVISOR,   null);
        seed("zoonotic.supervisor",     Role.ZOONOTIC_SUPERVISOR,null);
        seed("mining.supervisor",       Role.MINING_SUPERVISOR, null);
        seed("provincial.admin",        Role.PROVINCIAL_ADMIN,  null);
        seed("national.user",           Role.NATIONAL_USER,     null);
    }

    private void seed(String username, Role role, String ward) {
        repo.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setPasswordHash(enc.encode("password"));
            u.setFullName(username);
            u.setEmail(username + "@dpdms.uz.ac.zw");
            u.setRole(role);
            u.setWard(ward);
            return repo.save(u);
        });
    }
}