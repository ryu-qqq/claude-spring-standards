```java
@Component
public class {Bc}QueryAdapter implements {Bc}QueryPort {

    private final {Bc}QueryDslRepository repository;

    
    public {Bc}QueryAdapter({Bc}QueryDslRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public List<{Bc}> findAllByFileId(Long fileId) {
        List<{Bc}JpaEntity> entities = repository.findAllByFileId(fileId);

        return entities.stream()
            .map(ExtractedDataEntityMapper::toDomain)
            .toList();
    }
    
}
```