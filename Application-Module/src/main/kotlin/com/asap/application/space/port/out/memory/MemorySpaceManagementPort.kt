package com.asap.application.space.port.out.memory

import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
@Primary
class MemorySpaceManagementPort(

) : SpaceManagementPort {

    private val spaces = ConcurrentHashMap<String, SpaceEntity>()

    override fun getMainSpace(
        userId: DomainId
    ): MainSpace {
        val findSpace = spaces.filter {
            it.value.userId == userId.value
        }.values.sortedBy {
            it.createdAt
        }[0]

        return MainSpace(
            id = DomainId(findSpace.id)
        )
    }

    /**
     * 쿼리를 통해 index 결정해야함. 동시성 문제도 생각해야함
     * -> constraint를 걸어서 해결할 수 있을 것 같음
     * -> 전체 크기보다 커질 수 없음
     *
     * alter table space add constraint space_user_id_index unique (user_id, index);
     *
     * create table space(
     *    id varchar(255) primary key,
     *    user_id varchar(255),
     *    name varchar(255),
     *    template_type int,
     *    index int,
     *    created_at timestamp,
     *    updated_at timestamp
     *    constraint space_user_id_index unique (user_id, index)
     *    constraint space_user_id foreign key (user_id) references user(id)
     *    constraint space_index_size check (index < (select count(*) from space where user_id = '1'))
     * );
     *
     *
     * insert into space (index) values (
     *   select count(*)
     *   from space
     *   where user_id = '1'
     * );
     */
    override fun createSpace(
        userId: DomainId,
        spaceName: String,
        templateType: Int
    ): Space {
        val space = Space(
            userId = userId,
            name = spaceName,
            templateType = templateType
        )
        val spaceCount = spaces.filter {
            it.value.userId == userId.value
        }.size
        val spaceEntity = SpaceEntity(
            id = space.id.value,
            userId = userId.value,
            name = space.name,
            templateType = templateType,
            index = spaceCount
        )
        spaces[space.id.value] = spaceEntity
        return space
    }

    override fun getSpace(userId: DomainId, spaceId: DomainId): Space {
        val spaceEntity = spaces[spaceId.value] ?: throw IllegalArgumentException("Space not found")
        return Space(
            id = DomainId(spaceEntity.id),
            userId = DomainId(spaceEntity.userId),
            name = spaceEntity.name,
            templateType = spaceEntity.templateType
        )
    }

    override fun getAllIndexedSpace(userId: DomainId): List<IndexedSpace> {
        return spaces.filter {
            it.value.userId == userId.value
        }.values
            .map {
                IndexedSpace(
                    id = DomainId(it.id),
                    userId = DomainId(it.userId),
                    name = it.name,
                    index = it.index,
                    templateType = it.templateType
                )
            }
    }

    override fun update(space: Space): Space {
        val spaceEntity = spaces[space.id.value] ?: throw IllegalArgumentException("Space not found")
        val updatedSpaceEntity = spaceEntity.copy(
            name = space.name,
            updatedAt = LocalDateTime.now()
        )
        spaces[space.id.value] = updatedSpaceEntity
        return space
    }


    data class SpaceEntity(
        val id: String,
        val userId: String,
        val name: String,
        val templateType: Int,
        val index: Int = 0,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now()
    )

}