package sys.model.requests;

public record StudentUpdateRequest( String lastName, String firstName,
                                    String patronymic, String groupName) {
}
