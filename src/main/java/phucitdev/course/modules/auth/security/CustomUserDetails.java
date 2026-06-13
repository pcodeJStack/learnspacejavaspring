package phucitdev.course.modules.auth.security;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import phucitdev.course.modules.auth.entity.Account;
import java.util.Collection;
import java.util.List;
public class CustomUserDetails implements UserDetails {
    private final Account account;
    public CustomUserDetails(Account account){
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + account.getRole().name());
    }
    @Override
    public @Nullable String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
