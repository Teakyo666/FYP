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
      roles: ['admin', 'volunteer', 'recycler', 'customer'],
      order: 0
    }
  ],
  
  // 管理员专属菜单
  admin: [
    {
      path: '/dashboard/admin/garbage',
      name: 'AdminGarbage',
      component: 'dashboard/admin/garbage',
      icon: 'Delete',
      title: '垃圾管理',
      roles: ['admin'],
      order: 2
    },
    {
      path: '/dashboard/admin/user',
      name: 'AdminUser',
      component: 'dashboard/admin/user',
      icon: 'User',
      title: '用户管理',
      roles: ['admin'],
      order: 2
    },
    {
      path: '/dashboard/admin/AI',
      name: 'AdminAI',
      component: 'dashboard/admin/AI',
      icon: 'Robot',
      title: 'AI生成管理',
      roles: ['admin'],
      order: 2
    },
    {
      path: '/dashboard/admin/apply',
      name: 'AdminApply',
      component: 'dashboard/admin/apply',
      icon: 'Check',
      title: '申请管理',
      roles: ['admin'],
      order: 2
    },
    
  ],
  
  // 志愿者专属菜单
  volunteer: [
  ],
  
  // 回收员专属菜单
  recycler: [
  ],
  
  // 普通用户专属菜单
  customer: [
    
  ],
  
  // 通用功能菜单
  general: [
    {
      path: '/dashboard/garbage/sentence',
      name: 'GarbageSentence',
      component: 'dashboard/Garbage/sentence',
      icon: 'Message',
      title: '垃圾提示信息管理',
      roles: ['admin', 'volunteer'],
      order: 1
    },
    {
      path: '/dashboard/garbage/place',
      name: 'GarbagePlace',
      component: 'dashboard/Garbage/place',
      icon: 'Location',
      title: '垃圾地点管理',
      roles: ['admin', 'volunteer', 'recycler', 'customer'],
      order: 1
    },
    
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
  
  // 根据order属性排序
  menus.sort((a, b) => (a.order || 0) - (b.order || 0))
  
  return menus
}