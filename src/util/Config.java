package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import record.Node;
import record.Rule;

public class Config {
    /**
     * Parse nodes from yaml file 
     * @param element
     * @return
     */
    public static HashMap<String, Node> parseNodeMap(ArrayList<Map<String, Object>> element) {
        HashMap<String, Node> userMap = new HashMap<String, Node>();
        for (Map<String, Object> yamlNode : element) {
            try {
                String name = yamlNode.get("Name").toString();
                userMap.put(name, new Node(name, yamlNode.get("IP").toString(), (Integer)yamlNode.get("Port")));
            } catch (Exception e) {
                System.err.println("ERROR: configuration file error - " + yamlNode);
                e.printStackTrace();
            }
        }

        // for debug
        //System.out.println(userMap);
        return userMap;
    }
    
    public static String parseClockType (ArrayList<Map<String, Object>> element) {
    	String clockType = null;
    	for (Map<String, Object> yamlClock : element) {
            try {
                clockType = yamlClock.get("Type").toString();
            } catch (Exception e) {
                System.err.println("ERROR: configuration file error - " + yamlClock);
                e.printStackTrace();
            }
        }
    	return clockType;
    }
    
    public static HashMap<String, ArrayList<String>> parseGroupsHashMap (ArrayList<Map<String, Object>> element) {
    	HashMap<String, ArrayList<String>> groupsMap = new HashMap<String, ArrayList<String>>();
    	for (Map<String, Object> yamlGroup : element) {
    		try {
                String name = yamlGroup.get("Name").toString();
                ArrayList<String> members = (ArrayList<String>) yamlGroup.get("Members");
                groupsMap.put(name, members);
            } catch (Exception e) {
                System.err.println("ERROR: configuration file error - " + yamlGroup);
                e.printStackTrace();
            }
    	}
    	return groupsMap;
    }

    /**
     * Parse rules from yaml file
     * @param arrayList
     * @return
     */
    public static ArrayList<Rule> parseRules(ArrayList<Map<String, Object>> element) {
        ArrayList<Rule> ruleList = new ArrayList<Rule>();
        for (Map<String, Object> yamlRule : element) {
            try {
                Rule rule = new Rule();
                rule.setAction(yamlRule.get("Action").toString());
                if (yamlRule.containsKey("Src")) {
                    rule.setSrc(yamlRule.get("Src").toString());
                }
                if (yamlRule.containsKey("Dest")) {
                    rule.setDest(yamlRule.get("Dest").toString());
                }
                if (yamlRule.containsKey("Kind")) {
                    rule.setKind(yamlRule.get("Kind").toString());
                }
                if (yamlRule.containsKey("ID")) {
                    rule.setId((Integer)yamlRule.get("ID"));
                }
                if (yamlRule.containsKey("Nth")) {
                    rule.setNth((Integer)yamlRule.get("Nth"));
                }
                if (yamlRule.containsKey("EveryNth")) {
                    rule.setEveryNth((Integer)yamlRule.get("EveryNth"));
                }
                if (yamlRule.containsKey("Duplicate")) {
                	rule.setDuplicate(new Boolean(yamlRule.get("Duplicate").toString()));
                }
                
                ruleList.add(rule);
            } catch (Exception e) {
                System.err.println("ERROR: configuration file error - " + yamlRule);
                e.printStackTrace();
            }
        }

        // for debug
        //System.out.println(ruleList);
        return ruleList;
    }
}
