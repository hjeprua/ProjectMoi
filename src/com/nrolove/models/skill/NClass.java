package com.nrolove.models.skill;

import com.nrolove.models.Template.SkillTemplate;
import java.util.ArrayList;
import java.util.List;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class NClass {

    public int classId;

    public String name;
    
    public List<SkillTemplate> skillTemplatess = new ArrayList<>();
    
    public SkillTemplate getSkillTemplate(int tempId){
        for (SkillTemplate skillTemplate : skillTemplatess) {
            if (skillTemplate.id == tempId){
                return skillTemplate;
            }
        }
        return null;
    }
    
    public SkillTemplate getSkillTemplateByName(String name){
        for (SkillTemplate skillTemplate : skillTemplatess) {
            if((Util.removeAccent(skillTemplate.name).toUpperCase()).contains((Util.removeAccent(name)).toUpperCase())){
                return skillTemplate;
           }
        }
        return null;
    }
    
}
