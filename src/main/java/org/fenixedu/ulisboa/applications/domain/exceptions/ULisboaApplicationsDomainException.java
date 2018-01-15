package org.fenixedu.ulisboa.applications.domain.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.ulisboa.applications.util.ULisboaApplicationsUtil;

public class ULisboaApplicationsDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public ULisboaApplicationsDomainException(String key, String... args) {
        super(ULisboaApplicationsUtil.BUNDLE, key, args);
    }

    public ULisboaApplicationsDomainException(Status status, String key, String... args) {
        super(status, ULisboaApplicationsUtil.BUNDLE, key, args);
    }

    public ULisboaApplicationsDomainException(Throwable cause, String key, String... args) {
        super(cause, ULisboaApplicationsUtil.BUNDLE, key, args);
    }

    public ULisboaApplicationsDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, ULisboaApplicationsUtil.BUNDLE, key, args);
    }

    public static void throwWhenDeleteBlocked(Collection<String> blockers) {
        if (!blockers.isEmpty()) {
            throw new ULisboaApplicationsDomainException("key.return.argument", blockers.stream().collect(Collectors.joining(", ")));
        }
    }

}
