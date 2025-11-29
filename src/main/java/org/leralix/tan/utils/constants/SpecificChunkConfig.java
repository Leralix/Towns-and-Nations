package org.leralix.tan.utils.constants;

import org.leralix.tan.dataclass.territory.permission.RelationPermission;

public record SpecificChunkConfig(RelationPermission defaultRelation, boolean isLocked) {

}
