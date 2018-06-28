package cn.goudan.wang.controller;

import cn.goudan.wang.baseconfig.utils.Utils;
import cn.goudan.wang.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by MOMO-PC on 2017/4/11.
 */
@Controller
public class AdminController {
    /**
     * 管理员控制层
     */

    @Autowired
    private OAuthUserDetailsExtService userDetailsExtService;
    @Autowired
    private OAuthClientDetailsService clientDetailsService;
    @Autowired
    private OAuthRoleService roleService;

    /**
     * 访问管理中心首页
     */
    @RequestMapping("/admin/home")
    public ModelAndView home(ModelMap model) {
        return new ModelAndView("admin/home", model);
    }

    /**
     * 访问用户列表模块
     */
    @RequestMapping("/admin/userList")
    public ModelAndView userList(ModelMap model) {
        return new ModelAndView("admin/userList", model);
    }

    /**
     * 获取用户信息生成列表
     */
    @ResponseBody
    @RequestMapping(value = "/admin/getUserList")
    public List<OAuthUserDetails> getUserList() {
        return userDetailsExtService.getUsers();
    }

    /**
     * 访问客户管理模块
     */
    @RequestMapping("/admin/clientList")
    public String clientList(ModelMap model, Integer page, Integer rows) {
        if (page == null) {
            page = 1;
        }
        if (rows == null) {
            rows = 2;
        }
        int total = getClientCount();
        int pageCount = total % rows == 0 ? total / rows : total / rows + 1;
        model.addAttribute("rows", rows);
        model.addAttribute("clients", getClientList(page, rows));
        model.addAttribute("count", pageCount);
        model.addAttribute("page", page);
        return "admin/clientList";
    }

    @RequestMapping("/admin/clientAdd")
    public String clientAdd(ModelMap model) {
        OAuthClientDetails oAuthClientDetails = new OAuthClientDetails();
        oAuthClientDetails.setEnabled(true);
        oAuthClientDetails.setResourceIds(StringUtils.commaDelimitedListToSet("GOMRO"));
        long l = System.currentTimeMillis();
        int i = (int) (Math.random() * 999899) + 100;
        l += i;
        oAuthClientDetails.setClientSecret(Utils.extractTokenKey(String.valueOf(l)));
        model.addAttribute("client", oAuthClientDetails);
        model.addAttribute("users", userDetailsExtService.getUsers());
        return "admin/clientAdd";
    }

    @RequestMapping("/admin/userAdd")
    public String userAdd(ModelMap model) {
        OAuthUserDetails oAuthUserDetails = new OAuthUserDetails();
        model.addAttribute("user", oAuthUserDetails);
        return "admin/userAdd";
    }


    @RequestMapping("/admin/clientEdit/{client}")
    public String clientEdit(@PathVariable String client, ModelMap model) {
        model.addAttribute("client", clientDetailsService.getByClientId(client));
        model.addAttribute("users", userDetailsExtService.getUsers());
        return "admin/clientEdit";
    }

    @RequestMapping("/admin/userEdit/{user}")
    public String userEdit(@PathVariable String user, ModelMap model) {
        List<OAuthUserDetails> userDetails = getUserList();
        for (int i = 0; i < userDetails.size(); i++) {
            if (Long.valueOf(user).equals(userDetails.get(i).getId())) {
                model.addAttribute("user", userDetails.get(i));
                model.addAttribute("hasRole",getAUTHORITYListById(userDetails.get(i).getId()));
                break;
            }
        }
        model.addAttribute("role", getRoleList());
        return "admin/userEdit";
    }

    public List<Long> getAUTHORITYListById(Long l) {
        return roleService.getAuthorityById(l);
    }

    @RequestMapping("/admin/clientDelete/{client}")
    public String clientDelete(@PathVariable String client, ModelMap model, Integer page, Integer rows) {
        clientDetailsService.removeClientDetails(client);
        if (page == null) {
            page = 1;
        }
        if (rows == null) {
            rows = 2;
        }
        int total = getClientCount();
        int pageCount = total % rows == 0 ? total / rows : total / rows + 1;
        model.addAttribute("rows", rows);
        model.addAttribute("clients", getClientList(page, rows));
        model.addAttribute("count", pageCount);
        model.addAttribute("page", page);
        return "admin/clientList";
    }

    @RequestMapping(path = "/admin/AddClient", method = RequestMethod.POST)
    public String AddClient(@ModelAttribute(value = "client") OAuthClientDetails client, ModelMap model, Integer page, Integer rows) {
        client.setClientId(Utils.extractTokenKey(client.getClientId()));
        clientDetailsService.addOauthClientDetails(client);
        if (page == null) {
            page = 1;
        }
        if (rows == null) {
            rows = 2;
        }
        int total = getClientCount();
        int pageCount = total % rows == 0 ? total / rows : total / rows + 1;
        model.addAttribute("rows", rows);
        model.addAttribute("clients", getClientList(page, rows));
        model.addAttribute("count", pageCount);
        model.addAttribute("page", page);
        return "admin/clientList";
    }

    @RequestMapping(path = "/admin/addUser", method = RequestMethod.POST)
    public String addUser(@ModelAttribute(value = "user") OAuthUserDetails oAuthUserDetails, ModelMap model) {
        oAuthUserDetails.setPassword(new BCryptPasswordEncoder().encode(oAuthUserDetails.getPassword()));
        userDetailsExtService.addUser(oAuthUserDetails);
        return "admin/userList";
    }

    @RequestMapping(path = "/admin/clientEdit", method = RequestMethod.POST)
    public String editClient(@ModelAttribute(value = "client") OAuthClientDetails client, ModelMap model, Integer page, Integer rows) {
        clientDetailsService.updateOAuthClientDetails(client);
        if (page == null) {
            page = 1;
        }
        if (rows == null) {
            rows = 2;
        }
        int total = getClientCount();
        int pageCount = total % rows == 0 ? total / rows : total / rows + 1;
        model.addAttribute("rows", rows);
        model.addAttribute("clients", getClientList(page, rows));
        model.addAttribute("count", pageCount);
        model.addAttribute("page", page);
        return "admin/clientList";
    }

    @RequestMapping(path = "/admin/editUser", method = RequestMethod.POST)
    public String editUser(@ModelAttribute(value = "user") OAuthUserDetails oAuthUserDetails, String role, ModelMap model) {
        roleService.addAuthority(oAuthUserDetails, role);
        return "admin/userList";
    }
    /**
     * 获取客户信息生成列表
     */
    @ResponseBody
    @RequestMapping("/admin/getClientList")
    public List<OAuthClientDetails> getClientList(Integer page, Integer rows) {
        return clientDetailsService.getClients(page, rows);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/getClientCount")
    public Integer getClientCount() {
        return clientDetailsService.getClientsCount();
    }

    /**
     * 访问角色管理模块
     */
    @RequestMapping("/admin/roleList")
    public ModelAndView roleList(ModelMap model) {
        return new ModelAndView("admin/roleList", model);
    }

    /**
     * 获取角色信息生成列表
     */
    @ResponseBody
    @RequestMapping(value = "/admin/getRoleList")
    public List<OAuthRole> getRoleList() {
        return roleService.getRoles();
    }


    ///////////////////////

//    private ConsumerTokenServices tokenServices;

//    @Autowired
//    private TokenStore tokenStore;

//    private MemberApprovalHandler userApprovalHandler;
//
//    @RequestMapping("/oauth/cache_approvals")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void startCaching() throws Exception {
//        userApprovalHandler.setUseApprovalStore(true);
//    }
//
//    @RequestMapping("/oauth/uncache_approvals")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void stopCaching() throws Exception {
//        userApprovalHandler.setUseApprovalStore(false);
//    }

    /**
     * 根据用户和客户的信息确认来给予token会话
     *
     * @param client
     * @param user
     * @return
     */
//    @RequestMapping("/oauth/clients/{client}/users/{user}/tokens")
//    public Collection<OAuth2AccessToken> listTokensForUser(@PathVariable String client, @PathVariable String user,
//                                                           Principal principal) throws Exception {
//        checkResourceOwner(user, principal);
//        return enhance(tokenStore.findTokensByClientIdAndUserName(client, user));
//    }
//
//    /**
//     * 通过查找用户信息完成对token的移除
//     *
//     * @param user
//     * @return
//     */
//    @RequestMapping(value = "/oauth/users/{user}/tokens/{token}", method = RequestMethod.DELETE)
//    public ResponseEntity<Void> revokeToken(@PathVariable String user, @PathVariable String token, Principal principal)
//            throws Exception {
//        checkResourceOwner(user, principal);
//        if (tokenServices.revokeToken(token)) {
//            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    /**
//     * 通过查找客户信息中的clientid判断是否认证成功，
//     *
//     * @param client
//     * @return
//     */
//    @RequestMapping("/oauth/clients/{client}/tokens")
//    @ResponseBody
//    public Collection<OAuth2AccessToken> listTokensForClient(@PathVariable String client) throws Exception {
//        return enhance(tokenStore.findTokensByClientId(client));
//    }
//
//    private Collection<OAuth2AccessToken> enhance(Collection<OAuth2AccessToken> tokens) {
//        Collection<OAuth2AccessToken> result = new ArrayList<OAuth2AccessToken>();
//        for (OAuth2AccessToken prototype : tokens) {
//            DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(prototype);
//            OAuth2Authentication authentication = tokenStore.readAuthentication(token);
//            if (authentication == null) {
//                continue;
//            }
//            String clientId = authentication.getOAuth2Request().getClientId();
//            if (clientId != null) {
//                Map<String, Object> map = new HashMap<String, Object>(token.getAdditionalInformation());
//                map.put("client_id", clientId);
//                token.setAdditionalInformation(map);
//                result.add(token);
//            }
//        }
//        return result;
//    }
//
//    private void checkResourceOwner(String user, Principal principal) {
//        if (principal instanceof OAuth2Authentication) {
//            OAuth2Authentication authentication = (OAuth2Authentication) principal;
//            if (!authentication.isClientOnly() && !user.equals(principal.getName())) {
//                throw new AccessDeniedException(String.format("User '%s' cannot obtain tokens for user '%s'",
//                        principal.getName(), user));
//            }
//        }
//    }
}

