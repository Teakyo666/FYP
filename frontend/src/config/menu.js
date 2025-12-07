// src/config/menu.js
// 菜单配置文件

// 定义各个角色的菜单项
export const menuConfig = {
  // 所有用户都能看到的菜单
  common: [
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: 'dashboard/Home',
      icon: 'House',
      title: '仪表盘',
      roles: ['admin', 'volunteer', 'recycler', 'customer']
    }
  ],
  
  // 管理员专属菜单
  admin: [
    {
      path: '/dashboard/admin/garbage',
      name: 'AdminGarbage',
      component: 'admindashboard/Garbage',
      icon: 'Delete',
      title: '垃圾管理',
      roles: ['admin']
    },
    {
      path: '/dashboard/admin/user',
      name: 'AdminUser',
      component: 'admindashboard/User',
      icon: 'User',
      title: '用户管理',
      roles: ['admin']
    },
    
  ],
  
  // 志愿者专属菜单
  volunteer: [
    {
      path: '/dashboard/volunteer',
      name: 'VolunteerDashboard',
      component: 'volunteerdashboard',
      icon: 'Van',
      title: '志愿者中心',
      roles: ['volunteer']
    }
    
  ],
  
  // 回收员专属菜单
  recycler: [
    {
      path: '/dashboard/recycler',
      name: 'RecyclerDashboard',
      component: 'recyclerdashboard',
      icon: 'HelpFilled',
      title: '回收员中心',
      roles: ['recycler']
    },
  ],
  
  // 普通用户专属菜单
  customer: [
    {
      path: '/dashboard/customer',
      name: 'UserDashboard',
      component: 'userdashboard',
      icon: 'User',
      title: '个人中心',
      roles: ['customer']
    },
    
  ],
  
  // 通用功能菜单（所有登录用户都可以访问）
  general: [
    {
      path: '/dashboard/garbage',
      name: 'GarbageQuery',
      component: 'dashboard/Garbage/query',
      icon: 'Search',
      title: '垃圾分类查询',
      roles: ['admin', 'volunteer', 'recycler', 'customer']
    }
  ]
}

// 获取当前用户角色可访问的菜单
export const getUserMenus = (role) => {
  let menus = [].concat(menuConfig.common)
  
  switch (role) {
    case 'admin':
      menus = menus.concat(menuConfig.admin)
      break
    case 'volunteer':
      menus = menus.concat(menuConfig.volunteer)
      break
    case 'recycler':
      menus = menus.concat(menuConfig.recycler)
      break
    case 'customer':
      menus = menus.concat(menuConfig.customer)
      break
    default:
      break
  }
  
  // 添加通用功能菜单
  menus = menus.concat(menuConfig.general)
  
  return menus
}