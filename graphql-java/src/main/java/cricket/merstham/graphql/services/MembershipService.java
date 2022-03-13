package cricket.merstham.graphql.services;

import cricket.merstham.graphql.dto.AttributeDefinition;
import cricket.merstham.graphql.dto.Member;
import cricket.merstham.graphql.dto.MemberCategory;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.repository.AttributeDefinitionEntityRepository;
import cricket.merstham.graphql.repository.MemberCategoryEntityRepository;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class MembershipService {

    private final AttributeDefinitionEntityRepository attributeRepository;
    private final MemberEntityRepository memberRepository;
    private final MemberCategoryEntityRepository memberCategoryEntityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MembershipService(AttributeDefinitionEntityRepository attributeRepository, MemberEntityRepository memberRepository, MemberCategoryEntityRepository memberCategoryEntityRepository, ModelMapper modelMapper) {
        this.attributeRepository = attributeRepository;
        this.memberRepository = memberRepository;
        this.memberCategoryEntityRepository = memberCategoryEntityRepository;
        this.modelMapper = modelMapper;
    }

    public List<AttributeDefinition> getAttributes() {
        return attributeRepository.findAll()
                .stream()
                .map(a -> modelMapper.map(a, AttributeDefinition.class))
                .collect(Collectors.toList());
    }

    public List<Member> getMembers() {
        var members = memberRepository.findAll();
        return members
                .stream()
                .map(m -> modelMapper.map(m, Member.class))
                .collect(Collectors.toList());
    }

    public List<Member> getMembers(int start, int length) {
        return memberRepository.findAll(
                PageRequest.of(
                        start,
                        length))
                .map(m -> modelMapper.map(m, Member.class))
                .stream().collect(Collectors.toList());
    }


    public List<MemberCategory> getCategories(MemberCategoryWhereInput where) {
        var categories = memberCategoryEntityRepository.findAll();
        return categories
                .stream()
                .filter(category -> isNull(where) || where.matches(category))
                .map(c -> modelMapper.map(c, MemberCategory.class))
                .collect(Collectors.toList());
    }

    public Member getMember(int id) {
        return modelMapper.map(memberRepository.getReferenceById(id), Member.class);
    }
}
